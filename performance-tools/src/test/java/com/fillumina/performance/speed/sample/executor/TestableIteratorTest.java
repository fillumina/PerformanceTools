package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.infrastructure.RndTestable;
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
        TestableIterator it = new TestableIterator();

        it.register(new LfsrTestable());

        assertEquals(1, it.getCounter());
    }

    @Test
    public void shouldNotAddANewIndexIfSameClass() {
        TestableIterator it = new TestableIterator();

        it.register(new LfsrTestable());
        it.register(new LfsrTestable());

        assertEquals(1, it.getCounter());
    }

    @Test
    public void shouldAddANewIndexIfDifferentClass() {
        TestableIterator it = new TestableIterator();

        it.register(new LfsrTestable());
        it.register(new RndTestable());

        assertEquals(2, it.getCounter());
    }

    @Test
    public void shouldReturnTheIndexOfOneClass() {
        TestableIterator it = new TestableIterator();

        it.register(new LfsrTestable());
        it.register(new LfsrTestable());

        assertEquals(0, it.getIndexFor(LfsrTestable.class));
    }

    @Test
    public void shouldReturnTheIndexOfTwoClasses() {
        TestableIterator it = new TestableIterator();

        it.register(new LfsrTestable());
        it.register(new RndTestable());

        assertEquals(0, it.getIndexFor(LfsrTestable.class));
        assertEquals(1, it.getIndexFor(RndTestable.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotIterateOnNotRegisterdClass() {
        TestableIterator it = new TestableIterator();
        it.iterate(new LfsrTestable(), 10);
    }
}
