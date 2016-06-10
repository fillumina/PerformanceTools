package com.fillumina.performance.accuracy;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.formatter.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.formatter.StringTableStatsFormatter;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Assesses if dead code is effectively removed by Java runtime and if the
 * method to avoid that (using the return value) is effective.
 *
 * @author Francesco Illuminati
 */
public class TestableDeadCodeTest {
    private static final String DEAD_CODE = "dead code";
    private static final String REFERENCE = "reference";
    private static final String SINKED = "sinked";

    private boolean printOut = false;

    public static void main(final String[] args) {
        final TestableDeadCodeTest test = new TestableDeadCodeTest();
        test.printOut = true;
        test.shouldEliminateDeadCode();
    }

    @Test
    public void shouldEliminateDeadCode() {
        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt.addPerformanceConsumerIf(printOut, StringCsvSampleViewer.VIEWER);

        pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setMinConfidence(0.70)
                    .setMaxPercentageMargin(10)
                    .setTimeoutSeconds(30)
                .build())
            .addTest(DEAD_CODE, new AbstractTestable() {
                double d = 0d;

                @Override
                public Object test() {
                    // the following line is evicted
                    double x = sinTaylor(d);
                    d += 0.01;
                    return null;
                }
            })
            .addTest(SINKED, new AbstractTestable() {
                double d = 0d;

                @Override
                public Object test() {
                    double x = sinTaylor(d);
                    d += 0.01;
                    return x;
                }
            })

            // in some situations (such as with junit) dead code is not
            // optimized by the hotspot so this test is needed in order
            // to positively use for optimizations
            .addTest(REFERENCE, new AbstractTestable() {
                double d = 0d;

                @Override
                public Object test() {
                    d += 0.01;
                    return null;
                }
            })
            .addPerformanceConsumerIf(printOut, StringTableStatsFormatter.VIEWER)
            .execute()
            .use(AssertPerformance.withTolerance(20)
                .assertSpeed(DEAD_CODE).sameAs(REFERENCE)
                .assertSpeed(SINKED).slowerThan(DEAD_CODE))
            .printIf(printOut);
    }


    // for some reason the call to Math.sin() is not evicted even if dead
    // so this is the taylor expansion around 0 of sin(x)
    private double sinTaylor(final double d) {
        return d -
                pow(d, 3) / 6.0 +
                pow(d, 5) / 120.0 -
                pow(d, 7) / 5040.0 +
                pow(d, 9) / 362880.0 -
                pow(d, 11) / 39916800.0;
    }

    private static double pow(final double x, final int exponent) {
        if (exponent == 0) {
            return 1;
        }
        double result = x;
        for (int i=1; i<exponent; i++) {
            result *= x;
        }
        return result;
    }

    @Test
    public void shouldCalculatePow() {
        assertEquals(9, pow(3, 2), 0);
        assertEquals(8, pow(2, 3), 0);
        assertEquals(1, pow(12, 0), 0);
    }
}
