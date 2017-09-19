package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatioCalculatorTest {

    @Test
    public void shouldGetTheRatioBetweenTwoMeasures() {
        AssertableMock assertable =
                AssertableMock.create("first", 12.3, "second", 45.6);

        MeasureRatioCalculator calc = new MeasureRatioCalculator(assertable);

        assertEquals(1.0,
                calc.getRatio("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                calc.getRatio("first", Ratio.P_95).getValue(), 0);
    }

    @Test
    public void shouldGetTheRatioBetweenThreeMeasures() {
        AssertableMock assertable = AssertableMock.create(
                "first", 12.3, "second", 45.6, "third", 34.5);

        MeasureRatioCalculator calc = new MeasureRatioCalculator(assertable);

        assertEquals(1.0,
                calc.getRatio("second", Ratio.P_95).getValue(), 0);

        assertEquals(12.3 / 45.6,
                calc.getRatio("first", Ratio.P_95).getValue(), 0);

        assertEquals(34.5 / 45.6,
                calc.getRatio("third", Ratio.P_95).getValue(), 0);
    }

}
