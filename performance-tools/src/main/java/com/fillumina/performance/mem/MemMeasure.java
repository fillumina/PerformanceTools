package com.fillumina.performance.mem;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.LoggedDimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemMeasure extends LoggedDimensionalOnlineMeasure {
    private static final long serialVersionUID = 1L;

    public MemMeasure() {
        super(MemUnit.B);
    }

    public MemMeasure(double... values) {
        super(MemUnit.B, values);
    }

    public MemMeasure(Collection<? extends Number> collection) {
        super(MemUnit.B, collection);
    }

    public MemMeasure(Measure other) {
        super(MemUnit.B, other);
    }

    public MemMeasure(DimensionalMeasure other) {
        super(MemUnit.B, other);
    }

    public long getValue() {
        return (long) getMean();
    }

    public void assertEquals(long expected)
            throws AssertionError {
        assertEquals(expected, 0);
    }

    public void assertEquals(long expected, long delta)
            throws AssertionError {
        long result = getValue();
        if (expected < result - delta || expected > result + delta) {
            //TODO String.format(Locale.US,Locale.US,... ??
            String format = String.format(Locale.US,
                    "%sexpected %,d +/- %,d was %,d",
                    getLogMessages(), expected, delta, result);
            throw new AssertionError(format);
        }
    }

    public void assertLessThan(long expected) throws AssertionError {
        assertLessThan(expected, 0);
    }

    public void assertLessThan(long value, long delta)
            throws AssertionError {
        long result = getValue();
        if (result >= value - delta) {
            String format = String.format(Locale.US,
                    "%sexpected less than (%,d - %,d) was %,d",
                    getLogMessages(), value, delta, result);
            throw new AssertionError(format);
        }
    }

    public void assertGreaterThan(long value) throws AssertionError {
        assertGreaterThan(value, 0);
    }

    public void assertGreaterThan(long value, long delta)
            throws AssertionError {
        long result = getValue();
        if (result <= value + delta) {
            String format = String.format(Locale.US,
                    "%sexpected greater than (%,d + %,d) was %,d",
                    getLogMessages(), value, delta, result);
            throw new AssertionError(format);
        }
    }

}
