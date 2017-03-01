package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.FakePerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.testable.NullTestable;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.StaticPath;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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
    public static final int SAMPLES = 33;

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(WrapperSpeedStatsTableStringGenerator.VIEWER);
    }

    private static class MessageCheckerConsumer
            implements StatsProgressionStatusListener {

        @Override
        public void acceptStatsProgressionStatus(StaticPath name,
                SpeedStats stats, String rejectionMessage) {
            final int iterations = (int)stats.getPerformanceMap().get("first")
                            .getIterationsPerSample();
            final String errorMessage = "iterations: " + iterations +
                    " name= " + name;
            switch (iterations) {
                case 10:
                    assertNotNull(errorMessage, rejectionMessage);
                    break;

                case 100:
                    assertNotNull(errorMessage, rejectionMessage);
                    break;

                case 1000:
                    assertNull(errorMessage, rejectionMessage);
                    break;
            }
        }

    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceConsumer.<SpeedStats>instance());
    }

    private void iterate(final PerformanceConsumer<SpeedStats> consumer) {
        final Bag<Integer> countingMap = new Bag<>();

        FakePerformanceTimer fpt = new FakePerformanceTimer() {
            private final Random rnd = ThreadLocalRandom.current();

            @Override
            public SpeedSample createFakePerformances(int[] iterationArray) {
                int iterations = iterationArray[0];
                countingMap.add(iterations);
                if (iterations < 1_000) {
                    return createHighVarianceLoopPerformances(iterations);
                }
                return createStableLoopPerformances(iterations);
            }

            private SpeedSample createHighVarianceLoopPerformances(
                    final long iterations) {
                return FakePerformanceCreator.createSample(iterations,
                        new Object[][] {
                            {"first", rnd.nextInt(100)},
                            {"second", rnd.nextInt(100)},
                            {"full", rnd.nextInt(100)}
                        });
            }

            private SpeedSample createStableLoopPerformances(
                    final long iterations) {
                return FakePerformanceCreator.createSample(iterations,
                        new Object[][] {
                            {"first", 40},
                            {"second", 80},
                            {"full", 100}
                        });
            }
        };

        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setSamples(SAMPLES)
                    .setBaseIterations(10)
                    .setMaxPercentageMargin(0.05)
                    .setAutodiscoverBaseIterations(false)
                    .build()
                .addPerformanceConsumer(consumer);

        instrumenter.addStatsProgressionListener(new MessageCheckerConsumer());

        fpt.instrumentedBy(instrumenter)

            .addTest("first", NullTestable.INSTANCE)
            .addTest("second", NullTestable.INSTANCE)

            .execute();

        // while the performances have a variance greater than 0.4 it keeps incrementing
        assertEquals(SAMPLES, countingMap.getCount(10));
        assertEquals(SAMPLES, countingMap.getCount(100));
        assertEquals(SAMPLES, countingMap.getCount(1_000));

        // it stops at 10_000 iterations when the variance becomes 0
        assertEquals(0, countingMap.getCount(10_000));
    }
}
