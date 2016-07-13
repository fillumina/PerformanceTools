package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Before;
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

    private AtomicInteger counter = new AtomicInteger();
    private SpeedStats stats;

    @Before
    public void calculateLoopPerformances() {
        stats = PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(ITERATIONS_1, ITERATIONS_2)
                    .setSamples(SAMPLES)
                    .setEliminateOutliers(false)
                    .build())

            .addTest("check", new AbstractTestable() {

                @Override
                public Object test() {
                    counter.incrementAndGet();
                    PerformanceTimeHelper.sleepMicroseconds(INTERVAL_MS);
                    return null;
                }
            })

            .execute()

            .getPerformance();

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
                stats.getPerformances().get("check").getTotalIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        AssertHelper.assertEqualsWithinPercentage(
                "Wrong elapsed time reported",
                INTERVAL_NS,
                stats.getPerformances()
                        .get("check")
                        .getElapsedNanosecondsPerCycle()
                        .getMean(),
                15);
    }

    @Test
    public void shouldReportTheTheNanosecondsPerCycle() {
        AssertHelper.assertEqualsWithinPercentage("",
                INTERVAL_NS,
                stats.getPerformances()
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

    @Test(expected = IllegalStateException.class)
    public void shouldAutoProgressionPerformanceInstrumenterCheckNullInstrumentable() {
        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setMinConfidence(0.9)
                    .build();

        instrumenter.execute();
    }

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingIterations() {
        new IterationsProgressionChecker()
                .setSamples(10)
                .assertSamples(10, 100, 1000);

        new IterationsProgressionChecker()
                .setSamples(2)
                .assertSamples(5, 50, 500, 5000);

        new IterationsProgressionChecker()
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    private static class IterationsProgressionChecker {
        private int samples;

        public IterationsProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final int... iterations) {
            final AssertIterationsStatusListener statusListener =
                    new AssertIterationsStatusListener()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(iterations)
                        .setSamples(samples)
                        .setEliminateOutliers(false)
                        .setTimeout(30, TimeUnit.DAYS) // to allow debugging
                        .build())
                .addTest("counter", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return null;
                    }
                })
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
                .setSamples(10)
                .assertSamples(10, 100);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(5)
                .setMagnitude(3)
                .setSamples(2)
                .assertSamples(5, 50, 500);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(1)
                .setMagnitude(4)
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    private class BaseMagnitudeProgressionChecker {
        private int baseTimes;
        private int magnitude;
        private int samples;

        public BaseMagnitudeProgressionChecker setBaseTimes(int baseTimes) {
            this.baseTimes = baseTimes;
            return this;
        }

        public BaseMagnitudeProgressionChecker setMagnitude(int magnitude) {
            this.magnitude = magnitude;
            return this;
        }

        public BaseMagnitudeProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final int... iterations) {
            final AssertIterationsStatusListener statusListener =
                    new AssertIterationsStatusListener()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerFactory.createSingleThreaded()
                //.addPerformanceConsumerIf(true, StringCsvSampleViewer.VIEWER)
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setBaseAndMagnitude(baseTimes, magnitude)
                        .setEliminateOutliers(false)
                        .setSamples(samples)
                        .build())
                //.addPerformanceConsumerIf(true, StringCsvStatsFormatter.VIEWER)
                .addTest("counter", new AbstractTestable() {

                    @Override
                    public Object test() {
                        return null;
                    }
                })
                .addStatsProgressionListener(statusListener)
                .execute();

            statusListener.assertIterationsNumber(iterations.length);
        }
    }

}
