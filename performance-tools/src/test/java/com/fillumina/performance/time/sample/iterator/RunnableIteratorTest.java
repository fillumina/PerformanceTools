package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.RndRunnable;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.time.sample.iterator.RunnableIterator.Dispatcher;
import com.fillumina.performance.util.ToleranceAssertion;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * WARNING: very long test
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunnableIteratorTest {

    private final Dispatcher dispatcher = RunnableIterator.DISPATCHER;
    private boolean printout;

    /** methods and switch case generator. */
    public static void generateCode(final String[] args) {
        int max = 1024;
        // max number java methods in a class is 65535 according to
        // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#88659http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#88659
        for (int i=0; i<max; i++) {
            System.out.println("    private void " +
                    "l_" + i + "(Testable t, int l) " +
                    "{for (int i=0; i<l; i++) {t.test();}}");
        }
        System.out.println("\n\n");
        //            case 0: l_0(testable, iterations); break;
        for (int i=0; i<max; i++) {
            System.out.println("            case " + i + ": " +
                    "l_" + i + "(testable, iterations); break;");
        }
    }

    @Test
    public void shouldAddANewIndex() {
        int base = dispatcher.getCounter();

        dispatcher.getIterator(new LfsrRunnable());

        assertEquals(base + 1, dispatcher.getCounter());
    }

    @Test
    public void shouldAddTwoNewIndexesIfDifferentObjects() {
        int base = dispatcher.getCounter();

        dispatcher.getIterator(new LfsrRunnable());
        dispatcher.getIterator(new LfsrRunnable());

        assertEquals(base + 2, dispatcher.getCounter());
    }

    @Test
    public void shouldAddOneNewIndexIfSameObject() {
        int base = dispatcher.getCounter();

        final LfsrRunnable lfsrTestable = new LfsrRunnable();

        dispatcher.getIterator(lfsrTestable);
        dispatcher.getIterator(lfsrTestable);

        assertEquals(base + 1, dispatcher.getCounter());
    }

    @Test
    public void shouldAddANewIndexIfDifferentObjectsAndClasses() {
        int base = dispatcher.getCounter();

        dispatcher.getIterator(new LfsrRunnable());
        dispatcher.getIterator(new RndRunnable());

        assertEquals(base + 2, dispatcher.getCounter());
    }

    @Test
    public void shouldReturnTheIndexOfOneClass() {
        int base = dispatcher.getCounter();

        final LfsrRunnable one = new LfsrRunnable();
        final LfsrRunnable two = new LfsrRunnable();

        dispatcher.getIterator(one);
        dispatcher.getIterator(two);

        assertEquals(base + 0, dispatcher.getIndexFor(one));
        assertEquals(base + 1, dispatcher.getIndexFor(two));
    }

    @Test
    public void shouldReturnTheIndexOfTwoClasses() {
        final LfsrRunnable lfsr = new LfsrRunnable();
        final RndRunnable rnd = new RndRunnable();

        RunnableIterator lfsrIterator = dispatcher.getIterator(lfsr);
        RunnableIterator rndIterator = dispatcher.getIterator(rnd);

        assertEquals(lfsrIterator, dispatcher.getIterator(lfsr));
        assertEquals(rndIterator, dispatcher.getIterator(rnd));
    }

    @Test
    public void shouldIterate() {
        RunnableMock runnable = new RunnableMock();

        RunnableIterator it = dispatcher.getIterator(runnable);
        it.iterate(10);

        runnable.assertCalls(10);
    }

    public static void main(final String[] args) {
        final RunnableIteratorTest test = new RunnableIteratorTest();
        test.printout = true;

        System.out.println("Using same loop for all runnable");
        test.shouldUsingTheSameIteratorAffectTheFirstTest();

        System.out.println("");
        System.out.println("Using RunnableIterator");
        test.shouldUsingRunnableIteratorDoesntAffectTheFirstTest();
    }

    /*
    TEST SETUP: two classes extend the same interface so that if the first
    class is executed by calling the interface the JVM will inline the call
    but when the second class is executed as well the JVM must de-optimize
    the inlining (because there are 2 implementations now) so slowing the
    first class.
    This problem can be solved by using two completely different code path
    with different loops so that JVM can leave the code as is for each
    particular case.
    */

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    /*
     * These are two counters.
     */
    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    @Test
    public void shouldUsingTheSameIteratorAffectTheFirstTest() {
        Runnable a = () -> { Sink.drain(measure(c1)); };
        Runnable b = () -> { Sink.drain(measure(c2)); };

        final int iterations = 100_000_000;
        final int repetitions = 3;

        long la1 = loop(a, iterations, repetitions);
        print("a=" + la1);

        long lb1 = loop(b, iterations, repetitions);
        print("b=" + lb1);

        /*
        a and b use the same code but are different object and classes.
        when a is executed first the JVM optimizes it so that Counter.inc()
        calls directly Counter1.inc(). When b is executed this optimization
        must be removed because there are two implementations of Counter.
        That's why the second execution of a results slower.
         */
        long la2 = loop(a, iterations, repetitions);
        print("a=" + la2);

        assertTrue("la1=" + la1 + " >= la2=" + la2, la1 < la2);
    }

    private long loop(Runnable runnable, int iterations, int repetitions) {
        long start = System.nanoTime();
        for (int k=0; k<repetitions; k++) {
            for (int i=0; i<iterations; i++) {
                runnable.run();
            }
        }
        return System.nanoTime() - start;
    }

    Counter k1 = new Counter1();
    Counter k2 = new Counter2();

    @Ignore @Test
    public void shouldUsingRunnableIteratorDoesntAffectTheFirstTest() {
        RunnableIterator a = RunnableIterator.DISPATCHER.getIterator(
                () -> { Sink.drain(measure(k1)); });
        RunnableIterator b = RunnableIterator.DISPATCHER.getIterator(
                () -> { Sink.drain(measure(k2)); });

        // not too many iterations otherwise optimization would kick in
        final int iteration = 100_000_000;
        final int repetitions = 3;

        long la1 = loop(a, iteration, repetitions);
        print("a=" + la1);

        long lb1 = loop(b, iteration, repetitions);
        print("b=" + lb1);

        /*
        This time a and b use different loops and different codepaths. This
        helps the JVM to avoid de-optimizing the code.
        */
        long la2 = loop(a, iteration, repetitions);
        print("a=" + la2);

        /*
        la2 should be slightly faster because of optimizations
        */
        ToleranceAssertion.assertLess("la1=" + la1 + " <= la2=" + la2,
                la1, la2, Ratio.percentage(10));
    }

    private long loop(RunnableIterator iterator, int iterations, int repetitions) {
        long ns = 0;
        for (int k=0; k<repetitions; k++) {
            ns += iterator.measureIterationTimeNs(iterations);
        }
        return ns;
    }

    private void print(String s) {
        if (printout) {
            System.out.println(s);
        }
    }
}
