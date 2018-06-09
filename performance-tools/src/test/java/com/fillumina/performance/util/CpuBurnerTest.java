package com.fillumina.performance.util;

import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.util.stats.SimpleLinearRegression;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * WARNING: very long test
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO could it be possible to shorten the test duration?
public class CpuBurnerTest {

    public static void main(final String[] args) {
        main_elapsed(args);
    }

    public static void main_cycle(final String[] args) {
        PerformanceBuilder
                .config()
                    .speedConfig().end()
                    .tests()
                        .addTest(() -> CpuBurner.burn((long)1E9) )
                    .end()
                .end()
                .executeWithFullOutput();
    }

    public static void main_elapsed(final String[] args) {
        PerformanceBuilder
                .config()
                    .speedConfig().end()
                    .tests()
                        .addTest(() -> CpuBurner.burnMillis(3) )
                    .end()
                .end()
                .executeWithFullOutput();
    }

    @Test
    public void shouldCpuBurnBeLinear() {
        SimpleLinearRegression.Builder regressionBuilder =
                SimpleLinearRegression.builder();

        for (int i=1; i<10; i+=2) {
            double elapsedNs = measure(i * 1_000);
            regressionBuilder.add(i, elapsedNs);
        }

        SimpleLinearRegression slr = regressionBuilder.build();

        AssertHelper.assertEqualsWithinPercentage(
                "expected linear correlation but was " + slr.toString(),
                1.0, slr.getRSquared(), 5.0);
    }

    @Test
    public void shouldCpuBurnMillisBeAccurate() {
        double averageTimeMillis = PerformanceBuilder
                .config()
                    .speedConfig().end()
                    .tests()
                        .addTest(() -> CpuBurner.burnMillis(3) )
                    .end()
                .end()
                .executeWithoutOutput()
                .avgTime()
                .getStatsHolder()
                .getStats()
                .getFirstMeasure()
                .in(AverageTimeUnit.MILLISECONDS)
                .getMean();

        assertEquals(3.0, averageTimeMillis, 0.02);
    }

    private double measure(int cycles) {
        return PerformanceBuilder
                .config()
                    .speedConfig().end()
                    .tests()
                        .addTest(() -> CpuBurner.burn(cycles) )
                    .end()
                .end()
                .executeWithoutOutput()
                .avgTime()
                .getStatsHolder()
                .getStats()
                .getFirstMeasure()
                .getMean();
    }
}
