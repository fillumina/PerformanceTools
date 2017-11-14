package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Assesses if dead code is effectively removed by Java runtime and if the
 * method to avoid that (using the {@link SafeSink}) is effective.
 *
 * @author Francesco Illuminati
 */
public class TestableDeadCodeTest {
    private static final String DEAD_CODE = "dead code";
    private static final String REFERENCE = "reference";
    private static final String SINKED = "sinked";

    private Appendable printOut = null;

    public static void main(final String[] args) {
        final TestableDeadCodeTest test = new TestableDeadCodeTest();
        test.printOut = System.out;
        test.shouldEliminateDeadCode();
    }

    @Test
    public void shouldEliminateDeadCode() {
        final DefaultPerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();
        
        pt.addConsumerIf(printOut != null, SampleLineStringGenerator.VIEWER);

        pt.instrumentedBy(RequiredMarginStrategy.builder()
                .samples(10)
                .maxAllowedMargin(Ratio.percentage(5))
                .statsTimeout(IntervalUnit.MINUTES.quantity(2))
                .buildStatsProducer())
            .addTest(DEAD_CODE, new Runnable() {
                private double d = 0.0;

                @Override
                public void run() {
                    // it's evicted because x is not used
                    double x = sinTaylor(d);
                    d += 0.01;
                    SafeSink.drain(d);
                }
            })
            .addTest(SINKED, new Runnable() {
                private double d = 0.0;

                @Override
                public void run() {
                    // should not be evicted because x is sinked
                    double x = sinTaylor(d);
                    d += 0.01;
                    // the + operation time seems negligible
                    SafeSink.drain(d + x);
                }
            })

            // in some situations (such as with junit) dead code is not
            // evicted by the hotspot so this run is needed as a reference
            .addTest(REFERENCE, new Runnable() {
                private double d = 0d;

                @Override
                public void run() {
                    // simulates the evicted run
                    d += 0.01;
                    SafeSink.drain(d);
                }
            })
            .execute()
            .getStats(AverageTimeStats.class)
            .check(Assertions.withTolerance(Ratio.percentage(10))
                .assertOrder(DEAD_CODE).sameAs(REFERENCE)
                .assertOrder(SINKED).greaterThan(DEAD_CODE))
            .appendTo(printOut);
    }

    // this is the taylor expansion around 0 of sin(x)
    private static double sinTaylor(final double d) {
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
