package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceBuilder;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.stats.SimpleLinearRegression;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurnerTest {

    public static void main(final String[] args) {
        PerformanceBuilder
                .config()
                    .speed()
                    .tests()
                        .addTest(() -> {CpuBurner.burn((long)1E9);})
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
                .getMeasure()
                .getMean();
    }
}
