package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.stats.SingleMeasure;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalMeasureTest {

    @Test(expected = MismatchedUnitRuntimeException.class)
    public void shouldRjectDifferentUnits() {
        ImmutableDimensionalMeasure m = new ImmutableDimensionalMeasure(
                new SingleMeasure(1.0),
                Magnitude.KILO);

        m.in(Absolute.UNIT);
    }

    @Test
    public void shouldAdjustTheUnit() {
        ImmutableDimensionalMeasure m = new ImmutableDimensionalMeasure(
                new SingleMeasure(1.0),
                Magnitude.KILO);


        assertEquals(1E3, m.in(Magnitude.UNIT).getMean(), 0);
        assertEquals(1E6, m.in(Magnitude.MILLI).getMean(), 0);
        assertEquals(1E-3, m.in(Magnitude.MEGA).getMean(), 0);
    }

    @Test
    public void shouldSetBestUnit() {
        DimensionalMeasure dm1 = DimensionalMeasure.of(Magnitude.UNIT, 12000.0);
        DimensionalMeasure dm2 = DimensionalMeasure.of(Magnitude.KILO, 1);
        DimensionalMeasure dm3 = DimensionalMeasure.of(Magnitude.MEGA, 0.003);

        final Unit<?> bestUnit = DimensionalMeasure.bestUnit(dm1, dm2, dm3);

        assertEquals(Magnitude.KILO, bestUnit);
    }

    @Test
    public void shouldSumWithSimpleMeasure() {
        DimensionalMeasure dm = DimensionalMeasure.of(Magnitude.KILO, 1);
        Measure m = Measure.of(23);

        DimensionalMeasure result = dm.sum(m);

        assertEquals(Magnitude.KILO, result.getUnit());
        assertEquals(24, result.getMean(), 0);
    }

    @Test
    public void shouldSumWithDimensionalMeasure() {
        DimensionalMeasure dm1 = DimensionalMeasure.of(Magnitude.KILO, 1);
        DimensionalMeasure dm2 = DimensionalMeasure.of(Magnitude.UNIT, 7);

        DimensionalMeasure result = dm1.sum(dm2);

        assertEquals(Magnitude.KILO, result.getUnit());
        assertEquals(1.007, result.getMean(), 0);
    }

    @Test
    public void shouldSubtractWithSimpleMeasure() {
        DimensionalMeasure dm = DimensionalMeasure.of(Magnitude.KILO, 12);
        Measure m = Measure.of(7);

        DimensionalMeasure result = dm.subtract(m);

        assertEquals(Magnitude.KILO, result.getUnit());
        assertEquals(5, result.getMean(), 0);
    }

    @Test
    public void shouldSubtractWithDimensionalMeasure() {
        DimensionalMeasure dm1 = DimensionalMeasure.of(Magnitude.KILO, 12);
        DimensionalMeasure dm2 = DimensionalMeasure.of(Magnitude.UNIT, 7);

        DimensionalMeasure result = dm1.subtract(dm2);

        assertEquals(Magnitude.KILO, result.getUnit());
        assertEquals(11.993, result.getMean(), 0);
    }

    @Test
    public void shouldGetTheRatioWithDimensionalMeasure() {
        DimensionalMeasure dm1 = DimensionalMeasure.of(Magnitude.MILLI, 12_000);
        DimensionalMeasure dm2 = DimensionalMeasure.of(Magnitude.UNIT, 6);

        MeasureRatio mratio = dm1.ratio(dm2, Ratio.P_99);

        assertEquals(2.0, mratio.getValue(), 0);
    }
}
