package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.mock.PerformanceTimerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.rnd.XorShiftPlusRandom;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Validates if the auto progression algorithm converges.
 * The run has been made artificially converging after a given number
 of repetitions.
 *
 * @author Francesco Illuminati
 */
public class RepeatingStrategyTest {
    public static final int SAMPLES = 33;

    public static void main(final String[] args) {
        new RepeatingStrategyTest()
                .iterate(WrapperSpeedStatsTableStringGenerator.VIEWER);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCheckForNullInstrumentable() {
        final ConfigurableStatsProducer instrumenter =
                RepeatingStatsProducerBuilder.instance().build();

        instrumenter.execute();
    }

    @Test
    public void shouldProgressOverTwoSetOfIterations() {
        iterate(NullPerformanceConsumer.<SpeedStats>instance());
    }

    private void iterate(final PerformanceConsumer<SpeedStats> consumer) {
        final Bag<Integer> countingMap = new Bag<>();

        final ConfigurableStatsProducer instrumenter =
                RepeatingStatsProducerBuilder.instance()
                    .setSamples(SAMPLES)
                    .setBaseIterations(10)
                    .incrementIterations()
                    .setCoolDownCpu(false)
                    .setMaxPercentageMargin(Ratio.percentage(5))
                    .setAutodiscoverBaseIterations(false)
                    .build()
                .addPerformanceConsumer(consumer)
                .addStatsProgressionListener(new MessageCheckerConsumer());

        new MockPerformanceTimerImpl(countingMap)
            .instrumentedBy(instrumenter)
            .addTest("first", NullRunnable.INSTANCE)
//            .addTest("second", NullTestable.INSTANCE)
            .execute();

        // while the performances have a variance greater than 0.4 it keeps incrementing
        assertEquals(SAMPLES, countingMap.getCount(10));
        assertEquals(SAMPLES, countingMap.getCount(20));
        assertEquals(SAMPLES, countingMap.getCount(40));

        // it stops at 10_000 iterations when the variance becomes 0
        assertEquals(0, countingMap.getCount(2560));
    }

    private class MockPerformanceTimerImpl extends PerformanceTimerMock {
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
            return SpeedSampleMock.builder()
                    .addTest("first")
                        .iterations(iterations)
                        .nansecondsPerOp(rnd.nextInt(100))
                    .endTest()
                    .addTest("second")
                        .iterations(iterations)
                        .nansecondsPerOp(rnd.nextInt(100))
                    .endTest()
                    .addTest("full")
                        .iterations(iterations)
                        .nansecondsPerOp(rnd.nextInt(100))
                    .endTest()
                    .createSample();
        }

        private SpeedSample createStableLoopPerformances(
                final int iterations) {
            return SpeedSampleMock.builder()
                    .addTest("first")
                        .iterations(iterations)
                        .nansecondsPerOp(40)
                    .endTest()
                    .addTest("second")
                        .iterations(iterations)
                        .nansecondsPerOp(80)
                    .endTest()
                    .addTest("full")
                        .iterations(iterations)
                        .nansecondsPerOp(100)
                    .endTest()
                    .createSample();
        }
    }

    private static class MessageCheckerConsumer
            implements StatsProgressionStatusListener {

        @Override
        public void acceptStatsProgressionStatus(TName name,
                SpeedStats stats,
                String rejectionMessage) {

            final int iterations = (int) stats
                    .getSingleStatsMap()
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

        @Override
        public void acceptWarmupProgressionStatus(TName name, double speed) {
        }

    }
}
