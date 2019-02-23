package com.fillumina.performance.executor;

import com.fillumina.performance.util.pathname.PathName;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PNTest {

    @Test
    public void testTname_CharSequence() {
        PathName t = PN.pname("hello");
        assertEquals(PN.EMPTY.append("hello"), t);
    }

    @Test
    public void testNotNull_CharSequence() {
        CharSequence seq = null;
        PathName t = PN.pname(seq);
        assertEquals(t, PN.EMPTY);
    }

    @Test
    public void testTname_Iterable() {
        PathName t = PN.pname(Arrays.asList("one", "two", "three"));
        assertEquals(PN.EMPTY.append("one", "two", "three"), t);
    }

    @Test
    public void testNotNull_Iterable() {
        Iterable<String> iterable = null;
        PathName t = PN.pname(iterable);
        assertEquals(t, PN.EMPTY);
    }

    @Test
    public void testTname_StringArray() {
        String[] array = {"one", "two", "three"};
        PathName t = PN.pname(array);
        assertEquals(PN.EMPTY.append(array), t);
    }

    @Test
    public void testNotNull_StringArray() {
        String[] array = null;
        PathName t = PN.pname(array);
        assertEquals(t, PN.EMPTY);
    }

    @Test
    public void testNotNull() {
        PathName t = PN.notNull(null);
        assertEquals(t, PN.EMPTY);
    }

}
