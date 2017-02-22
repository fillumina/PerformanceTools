package com.fillumina.performance.util;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ComposedNameTest {

    private final ComposedName ROOT = ComposedName.createRoot();

    public static void main(final String[] args) {
        new ComposedNameTest().shouldCleanTheTree();
    }

    @Test
    public void shouldCleanTheTree() {
        ComposedName cn =
                ROOT.append("alfa").append("beta").append("delta");
        assertFalse(ROOT.isChildrenEmpty());

        cn = null;

        // force a GC to collect ROOT unused data
        Object[] array = new Object[10];
        for (int i=0; i<array.length; i++) {
            array[0] = new double[1<<24];
            System.gc();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {

            }
            ROOT.clean();
            if (ROOT.isChildrenEmpty()) {
                break; // ok!
            }
        }

        // could EVENTUALLY fail if GC fails to collect cn
        assertTrue(ROOT.isChildrenEmpty());
        cn = ROOT.append("another");
        assertEquals("another", cn.toString());
    }

    @Test
    public void shouldEmptyNameOutputNull() {
        assertEquals(null, ROOT.toString());
    }

    @Test
    public void shouldOutputTheSingleName() {
        assertEquals("name", ROOT.append("name").toString());
    }

    @Test
    public void shouldOutputDoubleNames() {
        assertEquals("alfa : beta",
                ROOT.append("alfa").append("beta").toString());
    }

    @Test
    public void shouldOutputTripleNames() {
        assertEquals("alfa : beta : delta",
                ROOT.append("alfa").append("beta").append("delta")
                        .toString());
    }

    @Test
    public void shouldNotCreateANewElementWithTheSameName() {
        ComposedName cn = ROOT.append("alfa");
        ComposedName beta = cn.append("beta");

        assertTrue(beta == cn.append("beta"));
    }

    @Test
    public void shouldReturnTheFirstName() {
        ComposedName cn =
                ROOT.append("alfa").append("beta").append("delta");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnTheFirstNameWithOnlyOneName() {
        ComposedName cn = ROOT.append("alfa");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnNullAsFirstNameIfEmpty() {
        assertEquals(null, ROOT.getFirstName());
    }

    @Test
    public void shouldReturnNullAsTheLastNameIfEmpty() {
        assertEquals(null, ROOT.getLastName());
    }

    @Test
    public void shouldReturnThePathAsList() {
        ComposedName cn =
                ROOT.append("alfa").append("beta").append("gamma");
        List<String> list = cn.asList();
        assertEquals("alfa", list.get(0));
        assertEquals("beta", list.get(1));
        assertEquals("gamma", list.get(2));
    }
}
