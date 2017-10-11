package com.fillumina.performance.util.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class QuantityTest {

    @Test
    public void shouldRecordADimensionAndReturnIt() {
        Quantity<IntervalUnit> time = new Quantity<>(10, IntervalUnit.MINUTES);
        assertEquals(10, time.getValue(), 0);
        assertEquals(IntervalUnit.MINUTES, time.getUnit());
    }

    @Test
    public void shouldConvertAValue() {
        Quantity<IntervalUnit> time = new Quantity<>(10, IntervalUnit.MINUTES);
        double result = time.in(IntervalUnit.SECONDS);
        assertEquals(600, result, 0);
    }

    @Test
    public void shouldCompare() {
        Quantity<IntervalUnit> min10 = new Quantity<>(10, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> sec60 = new Quantity<>(60, IntervalUnit.SECONDS);

        assertEquals(1, min10.compareTo(sec60));
        assertEquals(-1, sec60.compareTo(min10));
        assertEquals(0, min10.compareTo(min10));
        assertEquals(0, sec60.compareTo(sec60));
    }

    @Test
    public void shouldBeEqualsIfSameConstructor() {
        Quantity<IntervalUnit> a = new Quantity<>(10, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> b = new Quantity<>(10, IntervalUnit.MINUTES);

        assertEquals(a, b);
    }

    @Test
    public void shouldBeEqualsIfSameValue() {
        Quantity<IntervalUnit> a = new Quantity<>(10, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> b = new Quantity<>(600, IntervalUnit.SECONDS);

        assertEquals(a, b);
    }

    @Test
    public void shouldBeEqualsIfSameValueMagnitude() {
        Quantity<Magnitude> a = new Quantity<>(0.006240, Magnitude.KILO);
        Quantity<Magnitude> b = new Quantity<>(6240, Magnitude.MILLI);

        assertEquals(a, b);
    }

    @Test
    public void shouldNOTBeEqualsIfSameValueMagnitude() {
        Quantity<Magnitude> a = new Quantity<>(0.006240001, Magnitude.KILO);
        Quantity<Magnitude> b = new Quantity<>(6240, Magnitude.MILLI);

        assertNotEquals(a, b);
    }

    @Test
    public void shouldAdd() {
        Quantity<IntervalUnit> min1 = new Quantity<>(1, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> sec25 = new Quantity<>(25, IntervalUnit.SECONDS);

        Quantity<IntervalUnit> sum = min1.add(sec25);
        assertEquals(new Quantity<>(85, IntervalUnit.SECONDS), sum);
    }

    @Test
    public void shouldSubtract() {
        Quantity<IntervalUnit> min1 = new Quantity<>(1, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> sec25 = new Quantity<>(25, IntervalUnit.SECONDS);

        Quantity<IntervalUnit> sub = min1.subtract(sec25);
        assertEquals(new Quantity<>(35, IntervalUnit.SECONDS), sub);
    }

    @Test
    public void shouldMultiply() {
        Quantity<IntervalUnit> min1 = new Quantity<>(1, IntervalUnit.MINUTES);

        Quantity<IntervalUnit> mult = min1.multiply(6.5);
        assertEquals(new Quantity<>(390, IntervalUnit.SECONDS), mult);
    }

    @Test
    public void shouldDivide() {
        Quantity<IntervalUnit> min1 = new Quantity<>(1, IntervalUnit.MINUTES);

        Quantity<IntervalUnit> div = min1.divide(12);
        assertEquals(new Quantity<>(5, IntervalUnit.SECONDS), div);
    }

    @Test
    public void shouldGetString() {
        Quantity<IntervalUnit> min10 = new Quantity<>(10, IntervalUnit.MINUTES);
        Quantity<IntervalUnit> sec60 = new Quantity<>(60, IntervalUnit.SECONDS);

        assertEquals("10 m", min10.toString());
        assertEquals("1 m", sec60.toString());
    }

    @Test
    public void shouldCreateQuantitiesFromUnit() {
        Quantity<IntervalUnit> min10 = IntervalUnit.MINUTES.quantity(10);
        assertEquals(600, min10.in(IntervalUnit.SECONDS), 0);

        Quantity<Magnitude> kilo5 = Magnitude.KILO.quantity(5);
        assertEquals(5_000, kilo5.in(Magnitude.UNIT), 0);
    }
}
