package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IntervalUnitTest {

    @Test
    public void shouldConvertFromNsToSeconds() {
        double sec = IntervalUnit.SECONDS.convertFromBase(1E9);
        assertEquals(1, sec, 0);
    }

    @Test
    public void shouldConvertFromMsToSec() {
       double millis = IntervalUnit.MILLISECONDS.convert(1E6 * 1E9,
               IntervalUnit.SECONDS);
       assertEquals(1, millis, 0);
    }
}
