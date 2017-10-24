package com.fillumina.performance.util;

import com.fillumina.performance.util.Printable;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PrintableTest {
    private static final String STRING = "something";

    private static class PrintableImpl extends Printable<PrintableImpl> {

        @Override
        public PrintableImpl appendTo(Appendable appendable) {
            try {
                appendable.append(STRING);
            } catch (IOException ex) {
                throw new RuntimeException();
            }
            return this;
        }
    }

    @Test
    public void shouldAppendTo() {
        PrintableImpl printable = new PrintableImpl();
        StringBuilder buf = new StringBuilder();
        printable.appendTo(buf);
        assertEquals(STRING, buf.toString());
    }

    @Test
    public void shouldToString() {
        assertEquals(STRING, new PrintableImpl().toString());
    }

    @Test
    public void shouldAppendToIfTrue() {
        PrintableImpl printable = new PrintableImpl();
        StringBuilder buf = new StringBuilder();
        printable.appendToIf(true, buf);
        assertEquals(STRING, buf.toString());
    }

    @Test
    public void shouldNotAppendToIfFalse() {
        PrintableImpl printable = new PrintableImpl();
        StringBuilder buf = new StringBuilder();
        printable.appendToIf(false, buf);
        assertEquals("", buf.toString());
    }

}
