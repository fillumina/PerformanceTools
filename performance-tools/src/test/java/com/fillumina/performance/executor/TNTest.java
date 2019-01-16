package com.fillumina.performance.executor;

import com.fillumina.performance.util.tname.TName;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNTest {

    @Test
    public void testTname_CharSequence() {
        TName t = TN.tname("hello");
        assertEquals(TN.EMPTY.append("hello"), t);
    }

    @Test
    public void testTname_Iterable() {
        TName t = TN.tname(Arrays.asList("one", "two", "three"));
        assertEquals(TN.EMPTY.append("one", "two", "three"), t);
    }

    @Test
    public void testTname_StringArr() {
        String[] array = {"one", "two", "three"};
        TName t = TN.tname(array);
        assertEquals(TN.EMPTY.append(array), t);
    }

    @Test
    public void testNotNull() {
        TName t = TN.notNull(null);
        assertEquals(t, TN.EMPTY);
    }

}
