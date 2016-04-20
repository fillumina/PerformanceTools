package com.fillumina.performance.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.AssertHelper;
import com.fillumina.performance.util.PerformanceTimeHelper;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenterTest {
    // I have choosen prime numbers to avoid confusion
    public static final int ITERATIONS_1 = 11;
    public static final int ITERATIONS_2 = 101;
    public static final int SAMPLES = 13;
    public static final int INTERVAL_MS = 17;
    public static final int INTERVAL_NS = INTERVAL_MS * 1_000;

    private AtomicInteger counter = new AtomicInteger();
    private PerformanceStats stats;

    @Before
    public void calculateLoopPerformances() {
        stats = PerformanceTimerFactory.createSingleThreaded()

            .addTest("check", new AbstractTestable() {

                @Override
                public Object test() {
                    counter.incrementAndGet();
                    PerformanceTimeHelper.sleepMicroseconds(INTERVAL_MS);
                    return null;
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(ITERATIONS_1, ITERATIONS_2)
                    .setSamplesPerStep(SAMPLES)
                    .build())
                .execute()
                .getPerformanceStats();

        assertNotNull(stats);
    }

    @Ignore @Test // there is a new algorithm that repeat an iteration if stddev increase
    public void shouldIterateForAllTheProgressions() {
        assertEquals("Wrong number of iterations executed",
                (ITERATIONS_1 + ITERATIONS_2) * SAMPLES,
                counter.get());
    }

    /**
     * The iterations over which the performances are calculated
     * aren't the real ones but the average of those of the last
     * progression so the reported iterations are not
     * ITERATIONS_2 * SAMPLES
     * but only ITERATIONS_2.
     */
    @Test
    public void shouldCountOnlyTheIterationsOfTheLastProgression() {
        assertEquals("Wrong number of iterations reported",
                ITERATIONS_2,
                stats.getTestPerformances().values().iterator().next().getIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        assertEquals(10_000_000D, 1E7, 0);
        AssertHelper.assertEqualsWithinPercentage(
                "Wrong elapsed time reported",
                INTERVAL_NS * ITERATIONS_2,
                stats.getTestPerformances()
                        .get("check")
                        .getElapsedNanosecondsPerCycle()
                        .mean(),
                10);
    }

    @Test
    public void shouldReportTheTheNanosecondsPerCycle() {
        AssertHelper.assertEqualsWithinPercentage("",
                INTERVAL_NS,
                stats.getTestPerformances()
                        .values()
                        .iterator()
                        .next()
                        .getElapsedNanosecondsPerCycle()
                        .mean(),
                10);
    }
}
