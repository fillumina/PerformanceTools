package com.fillumina.performance.progression;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.executor.FakePerformanceTimer;
import com.fillumina.performance.stats.FakePerformanceCreator;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.NullTest;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * It's a way to validate if the auto progression algorithm converges.
 * The test has been made artificially converging after the given number
 * of iterations.
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterTest {
    public static final int SAMPLES = 10;

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceStatsConsumer.INSTANCE);
    }

    private void iterate(final PerformanceStatsConsumer consumer) {
        final Bag<Long> countingMap = new Bag<>();

        FakePerformanceTimer fpt = new FakePerformanceTimer() {
            private final Random rnd = ThreadLocalRandom.current();

            @Override
            public PerformanceSample createFakePerformances(final long iterations) {
                countingMap.add(iterations);
                if (iterations < 1_000) {
                    return createHighVarianceLoopPerformances(iterations);
                }
                return createStableLoopPerformances(iterations);
            }

            private PerformanceSample createHighVarianceLoopPerformances(
                    final long iterations) {
                return FakePerformanceCreator.createSample(iterations, new Object[][] {
                    {"first", rnd.nextInt(10)},
                    {"second", rnd.nextInt(20)},
                    {"full", 100}
                });
            }

            private PerformanceSample createStableLoopPerformances(
                    final long iterations) {
                return FakePerformanceCreator.createSample(iterations, new Object[][] {
                    {"first", 10},
                    {"second", 20},
                    {"full", 100}
                });
            }
        };

        fpt.addTest("first", NullTest.INSTANCE);
        fpt.addTest("second", NullTest.INSTANCE);

        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setTimeout(1, TimeUnit.DAYS) // to allow an easy debugging
                    .setBaseSamples(SAMPLES)
                    .setBaseIterations(10)
                    .setMinConfidence(0.9)
                    .setCheckConfidence(false)
                    .build()
                .addPerformanceConsumer(consumer);

        fpt.instrumentedBy(instrumenter)
            .execute();

        // while the performances have a variance greater than 0.4 it keeps incrementing
        assertEquals(SAMPLES, countingMap.getCount(10L));
        assertEquals(SAMPLES, countingMap.getCount(100L));
        assertEquals(SAMPLES, countingMap.getCount(1_000L));

        // it stops at 10_000 iterations when the variance becomes 0
        assertEquals(0, countingMap.getCount(10_000L));
    }
}
