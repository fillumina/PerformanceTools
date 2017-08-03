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
                .execWithFullOutput();
    }

    @Test
    public void shouldCpuBurnBeLinear() {
        SimpleLinearRegression.Builder builder =
                SimpleLinearRegression.builder();

        for (int i=1; i<10; i+=2) {
            double elapsedNs = measure(i * 1_000);
            builder.add(i, elapsedNs);
        }

        SimpleLinearRegression slr = builder.build();

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
                .execWithoutOutput()
                .avgTime()
                .getStatsHolder()
                .getAssertable()
                .getMeasure("test_0")
                .getMean();
    }
}
