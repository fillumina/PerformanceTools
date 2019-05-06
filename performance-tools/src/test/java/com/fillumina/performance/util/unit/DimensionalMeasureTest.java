package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.SingleMeasure;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DimensionalMeasureTest {

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
}
