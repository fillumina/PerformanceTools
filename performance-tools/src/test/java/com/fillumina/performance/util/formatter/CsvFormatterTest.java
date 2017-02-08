package com.fillumina.performance.util.formatter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CsvFormatterTest {

    @Test
    public void shouldAppendObjects() {
        assertEquals("1c2.1",
                new CsvFormatter().append(1, 'c', 2.1).toString());
    }

    @Test
    public void shouldAppendStrings() {
        assertEquals("onetwothree",
                new CsvFormatter().append("one", "two", "three").toString());
    }

    @Test
    public void shouldAppendLines() {
        assertEquals("one, two, three\nfour, five, six\n",
                new CsvFormatter()
                        .line("one", "two", "three")
                        .line("four", "five", "six")
                        .toString());
    }

    @Test
    public void shouldAppendLine() {
        assertEquals("1, two, c, 3.0",
                CsvFormatter.toString(1, "two", 'c', 3.0));
    }

}
