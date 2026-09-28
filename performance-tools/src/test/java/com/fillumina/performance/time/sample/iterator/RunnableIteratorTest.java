package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.RndRunnable;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.time.sample.iterator.RunnableIterator.Dispatcher;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
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

    @Test
    public void shouldKeepEachRunnableIsolatedAfterSwitching() {
        AtomicInteger firstCalls = new AtomicInteger();
        AtomicInteger secondCalls = new AtomicInteger();
        Runnable first = firstCalls::incrementAndGet;
        Runnable second = secondCalls::incrementAndGet;
        RunnableIterator firstIterator = dispatcher.getIterator(first);
        RunnableIterator secondIterator = dispatcher.getIterator(second);

        assertSame(first, firstIterator.getRunnable());
        assertSame(second, secondIterator.getRunnable());
        assertNotSame(firstIterator, secondIterator);
        firstIterator.measureIterationTimeNs(13);
        secondIterator.measureIterationTimeNs(17);
        firstIterator.measureIterationTimeNs(19);

        assertEquals(32, firstCalls.get());
        assertEquals(17, secondCalls.get());
    }

    public static void main(final String[] args) {
        final RunnableIteratorTest test = new RunnableIteratorTest();
        test.printout = true;

        System.out.println("Using same loop for all runnable");
        test.compareSharedLoopTimings();

        System.out.println("");
        System.out.println("Using RunnableIterator");
        test.compareDistinctIteratorTimings();
    }

    /*
    MANUAL EXPERIMENT: different implementations of one interface can cause
    the JIT to change its inlining decisions. Separate loop bodies provide
    different call sites, but their relative speed is JVM-dependent.
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

    // Manual JIT observation, not a cross-JVM timing guarantee.
    public void compareSharedLoopTimings() {
        Runnable a = () -> { Sink.drain(measure(c1)); };
        Runnable b = () -> { Sink.drain(measure(c2)); };

        final int iterations = 100_000_000;
        final int repetitions = 3;

        long la1 = loop(a, iterations, repetitions);
        print("a=" + la1);

        long lb1 = loop(b, iterations, repetitions);
        print("b=" + lb1);

        // A different implementation at the shared call site may change
        // how the JVM optimizes the second run of a.
        long la2 = loop(a, iterations, repetitions);
        print("a=" + la2);

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

    // Different loop bodies do not guarantee which execution is faster.
    public void compareDistinctIteratorTimings() {
        RunnableIterator a = RunnableIterator.DISPATCHER.getIterator(
                () -> { Sink.drain(measure(k1)); });
        RunnableIterator b = RunnableIterator.DISPATCHER.getIterator(
                () -> { Sink.drain(measure(k2)); });

        // not too many iterations otherwise optimization would kick in
        final int iteration = 500_000_000;
        final int repetitions = 3;

        long la1 = loop(a, iteration, repetitions);
        print("a=" + la1);

        long lb1 = loop(b, iteration, repetitions);
        print("b=" + lb1);

        // Separate loop bodies may reduce interference between call sites.
        long la2 = loop(a, iteration, repetitions);
        print("a=" + la2);

        // Report both timings without assuming either must be faster.
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
