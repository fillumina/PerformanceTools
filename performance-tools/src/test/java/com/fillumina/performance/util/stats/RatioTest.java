package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RatioTest {

    @Test
    public void shouldGetValue() {
        final double value = 123.456;
        Ratio r = Ratio.value(value);
        assertEquals(value, r.getValue(), 0);
    }

    @Test
    public void shouldGetPercentage() {
        final double value = 123.456;
        Ratio r = Ratio.value(value);
        assertEquals(value * 100, r.getPercentage(), 0);
    }

    @Test
    public void shouldSetPercentage() {
        final double value = 12.3456;
        Ratio r = Ratio.percentage(value);
        assertEquals(value, r.getPercentage(), 0);
    }

    @Test
    public void shouldSetPercentageAndGetValue() {
        final double value = 12.3456;
        Ratio r = Ratio.percentage(value);
        assertEquals(value, r.getValue() * 100.0, 0);
    }

    @Test
    public void shouldHaveEqualHashCode() {
        Ratio r1 = Ratio.value(12.3);
        Ratio r2 = Ratio.value(12.3);

        assertEquals(r1.hashCode(), r2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveEqualHashCode() {
        Ratio r1 = Ratio.value(12.3);
        Ratio r2 = Ratio.value(25.66);

        assertNotEquals(r1.hashCode(), r2.hashCode(), 0);
    }

    @Test
    public void shouldBeEquals() {
        Ratio r1 = Ratio.value(12.3);
        Ratio r2 = Ratio.value(12.3);

        assertEquals(r1, r2);
    }

    @Test
    public void shouldNotBeEquals() {
        Ratio r1 = Ratio.value(12.3);
        Ratio r2 = Ratio.value(12.8);

        assertNotEquals(r1, r2);
    }

    @Test
    public void testToString() {
        Ratio r = Ratio.value(0.567958);
        assertEquals("56.796 %", r.toString());
    }

}
