package com.fillumina.performance.util;

import java.util.Iterator;
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
    public void shouldEmptyNameOutputNull() {
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
                ComposedName.create("alfa").append("beta").append("delta").toString());
    }

    @Test
    public void shouldOutputTripleNamesFromEmpty() {
        assertEquals("alfa : beta : delta",
                ComposedName.EMPTY.append("alfa").append("beta").append("delta")
                        .toString());
    }

    @Test
    public void shouldIterate() {
        ComposedName cn = ComposedName.EMPTY
                .append("alfa").append("beta").append("gamma");
        Iterator<String> it = cn.iterator();
        assertEquals("alfa", it.next());
        assertTrue(it.hasNext());
        assertEquals("beta", it.next());
        assertTrue(it.hasNext());
        assertEquals("gamma", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void shouldNotCreateANewElementWithTheSameName() {
        ComposedName cn = ComposedName.create("alfa");
        ComposedName beta = cn.append("beta");

        assertTrue(beta == cn.append("beta"));
    }
}
