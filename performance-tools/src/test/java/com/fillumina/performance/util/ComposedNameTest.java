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
    public void shouldEmptyNameOutputNull() {
        assertNull(ComposedName.EMPTY.toString());
    }

    @Test
    public void shouldOutputTheSingleName() {
        assertEquals("name", ComposedName.EMPTY.add("name").toString());
    }

    @Test
    public void shouldOutputTheSingleNewName() {
        assertEquals("name", new ComposedName("name").toString());
    }

    @Test
    public void shouldOutputDoubleNames() {
        assertEquals("alfa : beta",
                new ComposedName("alfa").add("beta").toString());
    }

    @Test
    public void shouldOutputTripleNames() {
        assertEquals("alfa : beta : delta",
                new ComposedName("alfa").add("beta").add("delta").toString());
    }

    @Test
    public void shouldOutputTripleNamesFromEmpty() {
        assertEquals("alfa : beta : delta",
                ComposedName.EMPTY.add("alfa").add("beta").add("delta").toString());
    }

    @Test
    public void shouldIterate() {
        ComposedName cn = ComposedName.EMPTY
                .add("alfa").add("beta").add("delta");
        Iterator<String> it = cn.iterator();
        assertEquals("delta", it.next());
        assertTrue(it.hasNext());
        assertEquals("beta", it.next());
        assertTrue(it.hasNext());
        assertEquals("alfa", it.next());
        assertFalse(it.hasNext());
    }
}
