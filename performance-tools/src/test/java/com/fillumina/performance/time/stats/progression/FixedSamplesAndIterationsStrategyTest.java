package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.mock.AssertableConsumerMock;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.strgen.SpeedSampleLineStringGenerator;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.tname.TName;
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
    private static final int ITERATIONS_1 = 11;
    private static final int ITERATIONS_2 = 101;
    private static final int SAMPLES = 13;
    private static final int INTERVAL_US = 17;
    private static final int INTERVAL_NS = INTERVAL_US * 1_000;
    private static final AtomicInteger counter = new AtomicInteger();
    private static final TName TEST_NAME = TN.tname("check");

    private static boolean printout;
    private static TimeStats stats;

    public static void main(final String[] args) {
        FixedSamplesAndIterationsStrategyTest.printout = true;
        FixedSamplesAndIterationsStrategyTest.calculateLoopPerformances();
    }

    @BeforeClass
    public static void calculateLoopPerformances() {
        stats = PerformanceTimerFactory.createSingleThreaded()

            .addConsumerIf(printout,
                    SpeedSampleLineStringGenerator.VIEWER)

            .instrumentedBy(FixedSamplesAndIterationsStatsProducerBuilder
                    .instance()
                    .setIterations(ITERATIONS_1, ITERATIONS_2)
                    .setSamples(SAMPLES)
                    .setEliminateOutliers(false)
                    .setCoolDownCpu(false)
                    .build())

            .addConsumerIf(printout, TimeStatsStringGeneratorSelector.VIEWER)

            .addTest("check", () -> {
                counter.incrementAndGet();
                PerformanceTimeHelper.sleepMicroseconds(INTERVAL_US);
            })

            .execute()
            .getStats(AverageTimeStats.class)
            .printIf(printout)
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
                stats.getSingleStatsMap().get(TEST_NAME).getTotalIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        AssertHelper.assertEqualsWithinPercentage(
                "Wrong elapsed time reported",
                INTERVAL_NS,
                stats.getSingleStatsMap()
                        .get(TEST_NAME)
                        .getMeasure()
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
        final AssertableConsumerMock<TimeStats> consumer =
            new AssertableConsumerMock<>(TimeStats.class);

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(FixedSamplesAndIterationsStatsProducerBuilder
                    .instance()
                    .setIterations(5, 10)
                    .setCoolDownCpu(false)
                    .build())
                .addTest("example", NullRunnable.INSTANCE)
                .addConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

}
