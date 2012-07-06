package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForIterationsSuite;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is an example of a performance test that starts being favorable to
 * one test and ends, after being favorable to the other. In this
 * case the JVM optimizations have nothing to do with that but it's easy
 * to understand how JVM optimizations could impact performances in
 * the same way.
 *
 * @author fra
 */
public class ProgressionSequenceExampleTest {
    public static final double INCREMENT = Math.PI / 128;
    public static final double PERIOD = Math.PI * 2;

    private static class CachedSin {
        private final Map<Double, Double> map = new HashMap<>();

        private double sin(final double value) {
            double x = value % PERIOD;
            Double y = map.get(x);
            if (y == null) {
                y = Math.sin(x);
                map.put(x, y);
            }
            return y;
        }
    }

    @Test
    public void shoudPerformanceChangeWithIterations() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("direct", new Runnable() {
            double x = 0;

            @Override
            public void run() {
                assertTrue(Math.sin(x) != 2);
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

        final AssertPerformanceForIterationsSuite assertionSuite =
                createAssertionForIterationsSuite();

        new ProgressionSequence(pt)
                .setBaseAndMagnitude(1000, 3)
                .setSamplePerIterations(10)
                .setOnIterationPerformanceConsumer(assertionSuite)
                .executeSequence();

    }

    private AssertPerformanceForIterationsSuite createAssertionForIterationsSuite() {
        final AssertPerformanceForIterationsSuite assertionSuite =
                new AssertPerformanceForIterationsSuite();

        assertionSuite.addAssertionForIterations(1_000)
                .assertTest("cached").slowerThan("direct");

        assertionSuite.addAssertionForIterations(10_000)
                .assertTest("cached").slowerThan("direct");

        assertionSuite.addAssertionForIterations(100_000)
                .assertTest("cached").fasterThan("direct");

        return assertionSuite;
    }

    private static double increment(final double x) {
        double res = x + INCREMENT;
        if (res > PERIOD) {
            res = 0;
        }
        return res;
    }
}
