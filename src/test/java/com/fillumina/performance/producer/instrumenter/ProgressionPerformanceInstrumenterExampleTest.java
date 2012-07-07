package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForIterationsSuite;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is an example of a performance test that starts being favorable to
 * one test and ends being favorable to the other. In this
 * case the JVM optimizations have nothing to do with that but it's easy
 * to understand how they could impact performances in
 * the same way.
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenterExampleTest {
    public static final double INCREMENT = Math.PI / 128;
    public static final double PERIOD = Math.PI * 2;

    public static void main(final String[] args) {
        new ProgressionPerformanceInstrumenterExampleTest()
                .testWith(new StringCsvViewer());
    }

    private static class CachedSin {
        private final Map<Double, Double> cache = new HashMap<>();

        private double sin(final double x) {
            Double y = cache.get(x);
            if (y == null) {
                y = function(x);
                cache.put(x, y);
            }
            return y;
        }
    }

    @Test
    public void shoudPerformanceChangeWithIterations() {
        testWith(createAssertionForIterationsSuite());
    }

    private void testWith(final PerformanceConsumer consumer) {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("direct", new Runnable() {
            double x = 0;

            @Override
            public void run() {
                assertTrue(function(x) != 2);
                x = increment(x);
            }

        });

        pt.addTest("cached", new Runnable() {
            final CachedSin cachedSin = new CachedSin();
            double x = 0;

            @Override
            public void run() {
                assertTrue(cachedSin.sin(x) != 2);
                x = increment(x);
            }
        });

        pt
            .instrumentedBy(new ProgressionPerformanceInstrumenter())
            .setBaseAndMagnitude(1000, 3)
            .setSamplePerIterations(10)
            .setInnerPerformanceConsumer(consumer)
            .executeSequence();
    }

    private AssertPerformanceForIterationsSuite createAssertionForIterationsSuite() {
        final AssertPerformanceForIterationsSuite assertionSuite =
                new AssertPerformanceForIterationsSuite();

        assertionSuite.forIterations(1_000)
                .assertTest("cached").slowerThan("direct");

        assertionSuite.forIterations(10_000)
                .assertTest("cached").slowerThan("direct");

        assertionSuite.forIterations(100_000)
                .assertTest("cached").fasterThan("direct");

        return assertionSuite;
    }

    private static double function(final double x) {
        return 1.5 * Math.sin(x) / Math.tan(x/2);
    }

    private static double increment(final double x) {
        double res = x + INCREMENT;
        if (res > PERIOD) {
            res = 0;
        }
        return res;
    }
}
