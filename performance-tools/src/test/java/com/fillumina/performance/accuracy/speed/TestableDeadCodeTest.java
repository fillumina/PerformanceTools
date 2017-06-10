package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.speed.AssertSpeed;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.progression.RepeatingStatsProducerBuilder;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Assesses if dead code is effectively removed by Java runtime and if the
 * method to avoid that (using the return as sink) is effective.
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

        pt.addPerformanceConsumer(SampleLineStringGenerator.appendTo(printOut));

        pt.instrumentedBy(RepeatingStatsProducerBuilder.instance()
                    .setMaxPercentageMargin(Ratio.percentage(10))
                .build())
            .addTest(DEAD_CODE, new Runnable() {
                private double d = 0.0;

                @Override
                public void run() {
                    // is evicted because x is not used
                    double x = sinTaylor(d);
                    d += 0.01;
                    Sink.drain(d);
                }
            })
            .addTest(SINKED, new Runnable() {
                private double d = 0.0;

                @Override
                public void run() {
                    // should not be evicted because x is used
                    double x = sinTaylor(d);
                    d += 0.01;
                    Sink.drain(d + x);
                }
            })

            // in some situations (such as with junit) dead code is not
            // optimized by the hotspot so this run is needed in order
            // to check for optimizations
            .addTest(REFERENCE, new Runnable() {
                private double d = 0d;

                @Override
                public void run() {
                    // simulates the evicted run
                    d += 0.01;
                    Sink.drain(d);
                }
            })
            .addPerformanceConsumer(
                    WrapperSpeedStatsTableStringGenerator.appendTo(printOut))
            .execute()
            .check(AssertSpeed.withTolerance(Ratio.percentage(50))
                .assertOrder(DEAD_CODE).sameAs(REFERENCE)
                .assertOrder(SINKED).greaterThan(DEAD_CODE))
            .printTo(printOut);
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
