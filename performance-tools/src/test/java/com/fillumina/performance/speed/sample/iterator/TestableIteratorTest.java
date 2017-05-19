package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.speed.sample.iterator.RunnableIterator;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.RndRunnable;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableIteratorTest {

    // methods and switch case generator
    public static void main(final String[] args) {
        int max = 1024;
        // max java methods in a class is 65535 according to
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
        RunnableIterator it = new RunnableIterator();

        it.register(new LfsrRunnable());

        assertEquals(1, it.getCounter());
    }

    @Test
    public void shouldAddTwoNewIndexessIfDifferentObjects() {
        RunnableIterator it = new RunnableIterator();

        it.register(new LfsrRunnable());
        it.register(new LfsrRunnable());

        assertEquals(2, it.getCounter());
    }

    @Test
    public void shouldAddOneNewIndexessIfSameObject() {
        RunnableIterator it = new RunnableIterator();

        final LfsrRunnable lfsrTestable = new LfsrRunnable();

        it.register(lfsrTestable);
        it.register(lfsrTestable);

        assertEquals(1, it.getCounter());
    }

    @Test
    public void shouldAddANewIndexIfDifferentObjectsAndClasses() {
        RunnableIterator it = new RunnableIterator();

        it.register(new LfsrRunnable());
        it.register(new RndRunnable());

        assertEquals(2, it.getCounter());
    }

    @Test
    public void shouldReturnTheIndexOfOneClass() {
        RunnableIterator it = new RunnableIterator();
        final LfsrRunnable one = new LfsrRunnable();
        final LfsrRunnable two = new LfsrRunnable();

        it.register(one);
        it.register(two);

        assertEquals(0, it.getIndexFor(one));
        assertEquals(1, it.getIndexFor(two));
    }

    @Test
    public void shouldReturnTheIndexOfTwoClasses() {
        RunnableIterator it = new RunnableIterator();
        final LfsrRunnable lfsr = new LfsrRunnable();
        final RndRunnable rnd = new RndRunnable();

        it.register(lfsr);
        it.register(rnd);

        assertEquals(0, it.getIndexFor(lfsr));
        assertEquals(1, it.getIndexFor(rnd));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotIterateOnNotRegisterdClass() {
        RunnableIterator it = new RunnableIterator();
        it.iterate(new LfsrRunnable(), 10);
    }
}
