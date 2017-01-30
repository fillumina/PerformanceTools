package com.fillumina.performance.mem;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.LoggedDimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemMeasure extends LoggedDimensionalOnlineMeasure {
    private static final long serialVersionUID = 1L;

    public MemMeasure() {
        super(MemUnit.INSTANCE);
    }

    public MemMeasure(double... values) {
        super(MemUnit.INSTANCE, values);
    }

    public MemMeasure(Collection<? extends Number> collection) {
        super(MemUnit.INSTANCE, collection);
    }

    public MemMeasure(Measure other) {
        super(MemUnit.INSTANCE, other);
    }

    public MemMeasure(DimensionalMeasure other) {
        super(MemUnit.INSTANCE, other);
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
            String format = String.format(
                    "%sexpected %,d ± %,d was %,d",
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
            String format = String.format(
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
            String format = String.format(
                    "%sexpected greater than (%,d + %,d) was %,d",
                    getLogMessages(), value, delta, result);
            throw new AssertionError(format);
        }
    }

}
