package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class FixedSamplesAndIterationsStrategyTest {
    // prime numbers to avoid confusion
    public static final int ITERATIONS_1 = 11;
    public static final int ITERATIONS_2 = 101;
    public static final int SAMPLES = 13;
    public static final int INTERVAL_MS = 17;
    public static final int INTERVAL_NS = INTERVAL_MS * 1_000;

    private static AtomicInteger counter = new AtomicInteger();
    private static SpeedStats stats;

    private static final TName CHECK = TN.tname("check");

    @BeforeClass
    public static void calculateLoopPerformances() {
        stats = PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(FixedSamplesAndIterationsStatsProducerBuilder
                    .instance()
                    .setIterations(ITERATIONS_1, ITERATIONS_2)
                    .setSamples(SAMPLES)
                    .setEliminateOutliers(false)
                    .setCoolDownCpu(false)
                    .build())

            .addTest("check", () -> {
                counter.incrementAndGet();
                PerformanceTimeHelper.sleepMicroseconds(INTERVAL_MS);
            })

            .execute()
            .getAssertable();

        assertNotNull(stats);
    }

    @Test
    public void shouldIterateForAllTheProgressions() {
        assertEquals("Wrong number of iterations executed",
                (ITERATIONS_1 + ITERATIONS_2) * SAMPLES,
                counter.get());
    }

    @Test
    public void shouldCountOnlyTheIterationsOfTheLastProgression() {
        assertEquals("Wrong number of iterations reported",
                 ITERATIONS_2 * SAMPLES,
                stats.getSingleStatsMap().get(CHECK).getTotalIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        AssertHelper.assertEqualsWithinPercentage("Wrong elapsed time reported",
                INTERVAL_NS,
                stats.getSingleStatsMap()
                        .get(CHECK)
                        .getElapsedNanosecondsPerCycle()
                        .getMean(),
                15);
    }

    @Test
    public void shouldReportTheTheNanosecondsPerCycle() {
        AssertHelper.assertEqualsWithinPercentage("",
                INTERVAL_NS,
                stats.getSingleStatsMap()
                        .values()
                        .iterator()
                        .next()
                        .getElapsedNanosecondsPerCycle()
                        .getMean(),
                15);
    }

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingIterations() {
        new IterationsProgressionChecker()
                .assertIterations(5, 10, 15, 20);

        new IterationsProgressionChecker()
                .assertIterations(8, 16, 32);

        new IterationsProgressionChecker()
                .assertIterations(10, 20);

        new IterationsProgressionChecker()
                .assertIterations(8);
    }

    private static class IterationsProgressionChecker {
        private final int samples = 5;

        private void assertIterations(final int... iterations) {
            final AssertIterationsStatusListener statusListener =
                    new AssertIterationsStatusListener()
                        .setIterations(iterations)
                        .setSamples(samples);

            PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(FixedSamplesAndIterationsStatsProducerBuilder
                        .instance()
                        .setIterations(iterations)
                        .setSamples(samples)
                        .setEliminateOutliers(false)
                        .setCoolDownCpu(false)
                        .build())
                .addTest("test", NullRunnable.INSTANCE)
                .addStatsProgressionListener(statusListener)
                .execute();

            statusListener.assertIterationsNumber(iterations.length);
        }
    }

    @Test
    public void shouldCallConsumer() {
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer =
            new PerformanceConsumerExecutionChecker<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(FixedSamplesAndIterationsStatsProducerBuilder
                    .instance()
                    .setIterations(5, 10)
                    .setCoolDownCpu(false)
                    .build())
                .addTest("example", NullRunnable.INSTANCE)
                .addPerformanceConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

}
