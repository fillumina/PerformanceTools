package com.fillumina.performance.test;

import com.fillumina.performance.util.CpuBurner;
import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.stats.SimpleLinearRegression;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurnerTest {

    public static void main(final String[] args) {
        main_elapsed(args);
    }

    public static void main_cycle(final String[] args) {
        PerformanceBuilder
                .config()
                    .speed()
                    .tests()
                        .addTest(() -> {CpuBurner.burn((long)1E9);})
                    .end()
                .end()
                .executeWithFullOutput();
    }

    public static void main_elapsed(final String[] args) {
        PerformanceBuilder
                .config()
                    .speed()
                    .tests()
                        .addTest(() -> {CpuBurner.burnMillis(3);})
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
        double averageTimeNs = PerformanceBuilder
                .config()
                    .speed()
                    .tests()
                        .addTest(() -> {CpuBurner.burnMillis(3);})
                    .end()
                .end()
                .executeWithoutOutput()
                .avgTime()
                .getStatsHolder()
                .getAssertable()
                .getFirstMeasure()
                .getMean();

        assertEquals(3.0, averageTimeNs / 1E6, 0.02);
    }

    private double measure(int cycles) {
        return PerformanceBuilder
                .config()
                    .speed()
                    .tests()
                        .addTest(() -> {CpuBurner.burn(cycles);})
                    .end()
                .end()
                .executeWithoutOutput()
                .avgTime()
                .getStatsHolder()
                .getAssertable()
                .getFirstMeasure()
                .getMean();
    }
}
