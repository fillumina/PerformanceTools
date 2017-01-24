package com.fillumina.performance.util;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedNameTest {

    @Test
    public void shouldEmptyStringBuilderReturnEmptyString() {
        assertEquals("", new StringBuilder().toString());
    }

    @Test
    public void shouldEmptyNameOutputEmptyString() {
        assertEquals("", ComposedName.EMPTY.toString());
    }

    @Test
    public void shouldOutputTheSingleName() {
        assertEquals("name", ComposedName.EMPTY.append("name").toString());
    }

    @Test
    public void shouldOutputTheSingleNewName() {
        assertEquals("name", ComposedName.create("name").toString());
    }

    @Test
    public void shouldOutputDoubleNames() {
        assertEquals("alfa : beta",
                ComposedName.create("alfa").append("beta").toString());
    }

    @Test
    public void shouldOutputTripleNames() {
        assertEquals("alfa : beta : delta",
                ComposedName.create("alfa").append("beta").append("delta")
                        .toString());
    }

    @Test
    public void shouldOutputTripleNamesFromEmpty() {
        assertEquals("alfa : beta : delta",
                ComposedName.EMPTY.append("alfa").append("beta").append("delta")
                        .toString());
    }

    @Test
    public void shouldNotCreateANewElementWithTheSameName() {
        ComposedName cn = ComposedName.create("alfa");
        ComposedName beta = cn.append("beta");

        assertTrue(beta == cn.append("beta"));
    }

    @Test
    public void shouldCleanTheTree() {
        ComposedName cn =
                ComposedName.create("alfa").append("beta").append("delta");
        assertFalse(ComposedName.EMPTY.isEmptyNode());

        cn = null;
        Object[] array = new Object[10];
        for (int i=0; i<array.length; i++) {
            array[0] = new double[1<<24];
            System.gc();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {

            }
            ComposedName.EMPTY.clean();
            if (ComposedName.EMPTY.isEmptyNode()) {
                break; // ok!
            }
        }
        // could EVENTUALLY fail if GC fails to collect cn
        assertTrue(ComposedName.EMPTY.isEmptyNode());
        cn = ComposedName.create("another");
        assertEquals("another", cn.toString());
    }

    @Test
    public void shouldReturnTheFirstName() {
        ComposedName cn =
                ComposedName.create("alfa").append("beta").append("delta");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnTheFirstNameWithOnlyOneName() {
        ComposedName cn = ComposedName.create("alfa");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnTheFirstNameIfEmpty() {
        assertEquals("", ComposedName.EMPTY.getFirstName());
    }

    @Test
    public void shouldReturnTheLastNameIfEmpty() {
        assertEquals("", ComposedName.EMPTY.getLastName());
    }

    @Test
    public void shouldReturnThePathAsList() {
        ComposedName cn =
                ComposedName.create("alfa").append("beta").append("gamma");
        List<String> list = cn.asList();
        assertEquals("alfa", list.get(0));
        assertEquals("beta", list.get(1));
        assertEquals("gamma", list.get(2));
    }
}
