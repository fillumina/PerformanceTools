package com.fillumina.performance.util;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppendableWrapperTest {

    @Test
    public void shouldReturnIfIsNullAppendable() {
        assertTrue(new AppendableWrapper().isNullAppendable());
    }

    @Test
    public void shouldReturnIfIsNotNullAppendable() {
        assertFalse(new AppendableWrapper(System.out).isNullAppendable());
    }

    private static class InnerAppendable implements Appendable {

        StringBuilder buf = new StringBuilder();

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            buf.append(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws
                IOException {
            buf.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            buf.append(c);
            return this;
        }

        @Override
        public String toString() {
            return buf.toString();
        }
    }

    @Test
    public void testWriteOrNull() {
        Appendable innerAppendable = new InnerAppendable();
        AppendableWrapper wrapper = new AppendableWrapper(innerAppendable);
        wrapper.write("start '")
                .write(null)
                .write("' or '")
                .writeOrNull(null)
                .write("' ")
                .write(12)
                .write(" end");
        assertEquals("start '' or 'null' 12 end", innerAppendable.toString());
    }

}
