package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterTest {
    // prime numbers to avoid confusion
    public static final int ITERATIONS_1 = 11;
    public static final int ITERATIONS_2 = 101;
    public static final int SAMPLES = 13;
    public static final int INTERVAL_MS = 17;
    public static final int INTERVAL_NS = INTERVAL_MS * 1_000;

    private static AtomicInteger counter = new AtomicInteger();
    private static SpeedStats stats;

    @BeforeClass
    public static void calculateLoopPerformances() {
        stats = PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(ITERATIONS_1, ITERATIONS_2)
                    .setSamples(SAMPLES)
                    .setEliminateOutliers(false)
                    .setCoolDownCpu(false)
                    .build())

            .addTest("check", new Testable() {
                @Override
                public void test() {
                    counter.incrementAndGet();
                    PerformanceTimeHelper.sleepMicroseconds(INTERVAL_MS);
                }
            })
            .execute()
            .getStats();

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
                stats.getSingleStatsMap().get("check").getTotalIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        AssertHelper.assertEqualsWithinPercentage(
                "Wrong elapsed time reported",
                INTERVAL_NS,
                stats.getSingleStatsMap()
                        .get("check")
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

    @Test(expected = IllegalStateException.class)
    public void shouldProgressionPerformanceInstrumenterCheckNullInstrumentable() {
        final ProgressionPerformanceInstrumenter instrumenter =
                ProgressionPerformanceInstrumenter.builder()
                    .setBaseAndMagnitude(10, 1)
                    .build();

        instrumenter.execute();
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
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(iterations)
                        .setSamples(samples)
                        .setEliminateOutliers(false)
                        .setCoolDownCpu(false)
                        .build())
                .addTest("test", NullTestable.INSTANCE)
                .addStatsProgressionListener(statusListener)
                .execute();

            statusListener.assertIterationsNumber(iterations.length);
        }
    }

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingBaseAndMagnitude() {
        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(10)
                .setMagnitude(2)
                .assertIterations(10, 100);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(5)
                .setMagnitude(3)
                .assertIterations(5, 50, 500);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(1)
                .setMagnitude(4)
                .assertIterations(1, 10, 100, 1000);

    }

    private class BaseMagnitudeProgressionChecker {
        private final int samples = 5;
        private int baseTimes;
        private int magnitude;

        public BaseMagnitudeProgressionChecker setBaseTimes(int baseTimes) {
            this.baseTimes = baseTimes;
            return this;
        }

        public BaseMagnitudeProgressionChecker setMagnitude(int magnitude) {
            this.magnitude = magnitude;
            return this;
        }

        private void assertIterations(final int... iterations) {
            final AssertIterationsStatusListener statusListener =
                    new AssertIterationsStatusListener()
                        .setIterations(iterations)
                        .setSamples(samples);

            PerformanceTimerFactory.createSingleThreaded()
                //.addPerformanceConsumerIf(true, StringCsvSampleViewer.VIEWER)
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setBaseAndMagnitude(baseTimes, magnitude)
                        .setEliminateOutliers(false)
                        .setCoolDownCpu(false)
                        .setSamples(samples)
                        .build())
                //.addPerformanceConsumerIf(true, StringCsvStatsFormatter.VIEWER)
                .addTest("test", NullTestable.INSTANCE)
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
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(5, 10)
                    .setCoolDownCpu(false)
                    .build())
                .addTest("example", NullTestable.INSTANCE)
                .addPerformanceConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

}
