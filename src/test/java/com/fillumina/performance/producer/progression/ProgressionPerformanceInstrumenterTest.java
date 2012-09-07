package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.util.JunitAssertHelper;
import com.fillumina.performance.util.PerformanceTimeHelper;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenterTest {
    // I have choosen prime numbers to avoid confusion
    public static final int ITERATIONS_1 = 11;
    public static final int ITERATIONS_2 = 101;
    public static final int SAMPLES = 13;
    public static final int INTERVAL_MS = 17;
    public static final int INTERVAL_NS = INTERVAL_MS * 1_000;

    private AtomicInteger counter = new AtomicInteger();
    private LoopPerformances loopPerformances;

    @Before
    public void calculateLoopPerformances() {
        loopPerformances = PerformanceTimerBuilder.createSingleThread()

            .addTest("check", new Runnable() {

                @Override
                public void run() {
                    counter.incrementAndGet();
                    PerformanceTimeHelper.sleepMicroseconds(INTERVAL_MS);
                }
            })

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                .setIterationProgression(ITERATIONS_1, ITERATIONS_2)
                .setSamplesPerMagnitude(SAMPLES)
                .build()
                .execute()
                .getLoopPerformances();

    }

    @Test
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
                loopPerformances.getIterations());
    }

    @Test
    public void shouldReportTheElapsedTime() {
        assertEquals(10_000_000D, 1E7, 0);
        JunitAssertHelper.assertEqualsWithinPercentage(
                "Wrong elapsed time reported",
                INTERVAL_NS * ITERATIONS_2,
                loopPerformances.get("check").getElapsedNanoseconds(),
                10);
    }

    @Test
    public void shouldReportTheTheNanosecondsPerCycle() {
        JunitAssertHelper.assertEqualsWithinPercentage("",
                INTERVAL_NS,
                loopPerformances.getNanosecondsPerCycleList().get(0),
                10);
    }
}
