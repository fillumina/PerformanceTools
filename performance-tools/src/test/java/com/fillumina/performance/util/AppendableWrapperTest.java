package com.fillumina.performance.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableWrapperTest {

    @Test
    public void testWriteOrNull() {
        StringBuilder buf = new StringBuilder();
        AppendableWrapper wrapper = new AppendableWrapper(buf);
        wrapper.append("start '")
                .append(null)
                .append("' ")
                .append(12)
                .append(" end");
        assertEquals("start 'null' 12 end", buf.toString());
    }

}
