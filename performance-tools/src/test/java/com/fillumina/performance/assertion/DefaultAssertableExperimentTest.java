package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Quantity;
import java.io.IOException;
import java.util.NoSuchElementException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 * This is an example on how this package could be used.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultAssertableExperimentTest {

    public static void main(final String[] args) throws IOException {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .assertValue("Kenny").equalsTo(5)
                .appendTo(System.out, votes);
    }

    @Test
    public void shouldCreate() {
        DefaultAssertableExperiment votes = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .assertValue("Kenny").equalsTo(4)
                .check(votes);
    }

    @Test
    public void shouldTakeDimensionalMeasure() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(6.5, 7.0, 5.5));

        assertEquals(6.333, votes.getMeasure("Carl").getMean(), 0.001);
    }

    @Test
    public void shouldTakeMeasure() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Kenny", Measure.of(4, 6.0, 5.5));

        assertEquals(5.167, votes.getMeasure("Kenny").getMean(), 0.001);
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldThrowAnExceptionIfMeasureNotFound() {
        DefaultAssertableExperiment votes = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        votes.getMeasure("not_existent");
    }

    @Test
    public void shouldGetTheMeasure() {
        DefaultAssertableExperiment votes = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        Measure carl = votes.getMeasure("Carl");
        assertEquals(6.5, carl.getMean(), 0);

        Measure lola = votes.getMeasure("Lola");
        assertEquals(7.0, lola.getMean(), 0);

        Measure kenny = votes.getMeasure("Kenny");
        assertEquals(4, kenny.getMean(), 0);
    }

    @Test
    public void shouldBeEquals() {
        DefaultAssertableExperiment a = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        DefaultAssertableExperiment b = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        assertEquals(a, b);
    }

    @Test
    public void shouldNotBeEquals() {
        DefaultAssertableExperiment a = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        DefaultAssertableExperiment b = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 3.2);

        assertNotEquals(a, b);
    }

    @Test
    public void shouldHaveSameHashCode() {
        DefaultAssertableExperiment a = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        DefaultAssertableExperiment b = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        DefaultAssertableExperiment a = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 4);

        DefaultAssertableExperiment b = DefaultAssertableExperiment.create(
                "Carl", 6.5, "Lola", 7.0, "Kenny", 3.2);

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void shouldCompare() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .assertValue("Kenny").equalsTo(5)
                .check(votes);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldCompareValueWithDifferentUnitBad() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 6.5, 7.0, 5.5))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 7.0, 8.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertQuantity("Kenny").equalsTo(Quantity.of(5, Magnitude.UNIT))
                .check(votes);
    }

    @Test
    public void shouldCompareValueWithDifferentUnitOK() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 6.5, 7.0, 5.5))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 7.0, 8.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertQuantity("Kenny").equalsTo(Quantity.of(5, Magnitude.MILLI))
                .check(votes);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldCompareOrderWithDifferentUnitBad() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 6.5, 7.0, 5.5))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 7.0, 8.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .check(votes);
    }

    @Test
    public void shouldCompareOrderWithDifferentUnitOk() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 6.5, 7.0, 5.5))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 7.0, 8.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").greaterThan("Lola")
                .check(votes);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldComparePercentageWithDifferentUnitBad() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 1.0))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 70.0, 80.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertRatioPercentage("Lola").greaterThan(80)
                .check(votes);
    }

    @Test
    public void shouldComparePercentageWithDifferentUnitOk() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", DimensionalMeasure.of(Magnitude.KILO, 1.0))
                .add("Lola", DimensionalMeasure.of(Magnitude.UNIT, 70.0, 80.0))
                .add("Kenny", DimensionalMeasure.of(Magnitude.MILLI, 4, 6.0, 5.5));

        Assertions.withTolerance(Ratio.percentage(25))
                .assertRatioPercentage("Lola").equalsTo(7.5)
                .check(votes);
    }

    @Test(expected=ExperimentAssertionError.class)
    public void shouldCheckException() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").greaterThan("Lola")
                .check(votes);
    }
}
