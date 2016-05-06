package com.fillumina.performance.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
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

        pt.addTest(DEAD_CODE, new AbstractTestable() {
            double d = 0d;

            @Override
            public Object test() {
                // the following line is evicted
                double x = sinTaylor(d);
                d += 0.01;
                return null;
            }
        });

        pt.addTest(SINKED, new AbstractTestable() {
            double d = 0d;

            @Override
            public Object test() {
                double x = sinTaylor(d);
                d += 0.01;
                return x;
            }
        });

        pt.addTest(REFERENCE, new AbstractTestable() {
            double d = 0d;

            @Override
            public Object test() {
                d += 0.01;
                return null;
            }
        });

        pt.addPerformanceSampleConsumerIf(printOut, StringCsvSampleViewer.INSTANCE);

        pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                .setBaseIterations(10_000)
                .setBaseSamples(500)
                .setMinConfidence(0.70)
                .setTimeout(1, TimeUnit.DAYS)
                .build())
            .addPerformanceConsumerIf(printOut, StringTableStatsViewer.INSTANCE)
            .execute()
            .use(AssertPerformance.withTolerance(15)
                .assertTest(DEAD_CODE).sameAs(REFERENCE)
                .assertTest(SINKED).slowerThan(DEAD_CODE))
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
