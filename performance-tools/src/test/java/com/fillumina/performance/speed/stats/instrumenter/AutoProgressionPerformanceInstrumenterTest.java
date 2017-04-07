package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.mock.MockPerformanceTimer;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.rnd.XorShiftPlusRandom;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Validates if the auto progression algorithm converges.
 * The test has been made artificially converging after a given number
 * of repetitions.
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenterTest {
    public static final int SAMPLES = 33;

    public static void main(final String[] args) {
        new AutoProgressionPerformanceInstrumenterTest()
                .iterate(WrapperSpeedStatsTableStringGenerator.VIEWER);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCheckForNullInstrumentable() {
        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .build();

        instrumenter.execute();
    }

    @Test
    public void shouldCallConsumer() {
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer =
            new PerformanceConsumerExecutionChecker<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setBaseIterations(10)
                        .setMaxPercentageMargin(100)
                        .setCoolDownCpu(false)
                    .build())
                .addTest("example", new LfsrTestable())
                .addPerformanceConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceConsumer.<SpeedStats>instance());
    }

    private void iterate(final PerformanceConsumer<SpeedStats> consumer) {
        final Bag<Integer> countingMap = new Bag<>();

        PerformanceTimer pt = new MockPerformanceTimerImpl(countingMap);

        final AutoProgressionPerformanceInstrumenter instrumenter =
                AutoProgressionPerformanceInstrumenter.builder()
                    .setSamples(SAMPLES)
                    .setBaseIterations(10)
                    .setCoolDownCpu(false)
                    .setMaxPercentageMargin(0.05)
                    .setAutodiscoverBaseIterations(false)
                    .build()
                .addPerformanceConsumer(consumer)
                .addStatsProgressionListener(new MessageCheckerConsumer());

        pt.instrumentedBy(instrumenter)
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

    private class MockPerformanceTimerImpl extends MockPerformanceTimer {
        private final Bag<Integer> countingMap;
        private final Random rnd = new XorShiftPlusRandom();

        public MockPerformanceTimerImpl(Bag<Integer> countingMap) {
            this.countingMap = countingMap;
        }

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
                final int iterations) {
            return MockPerformanceCreator.speedSampleBuilder()
                    .addTest("first")
                        .iterations(iterations)
                        .timePerOp(rnd.nextInt(100))
                    .endTest()
                    .addTest("second")
                        .iterations(iterations)
                        .timePerOp(rnd.nextInt(100))
                    .endTest()
                    .addTest("full")
                        .iterations(iterations)
                        .timePerOp(rnd.nextInt(100))
                    .endTest()
                    .createSample();
        }

        private SpeedSample createStableLoopPerformances(
                final int iterations) {
            return MockPerformanceCreator.speedSampleBuilder()
                    .addTest("first")
                        .iterations(iterations)
                        .timePerOp(40)
                    .endTest()
                    .addTest("second")
                        .iterations(iterations)
                        .timePerOp(80)
                    .endTest()
                    .addTest("full")
                        .iterations(iterations)
                        .timePerOp(100)
                    .endTest()
                    .createSample();
        }
    }

    private static class MessageCheckerConsumer
            implements StatsProgressionStatusListener {

        @Override
        public void acceptStatsProgressionStatus(StaticPath name,
                SpeedStats stats,
                String rejectionMessage) {

            final int iterations = (int) stats
                    .getPerformanceMap()
                    .get("first")
                    .getIterationsPerSample();

            final String errorMessage =
                    "iterations: " + iterations + " name= " + name;

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
}
