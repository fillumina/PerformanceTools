package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.mock.PerformanceTimerMock;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer.Configuration;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer.Strategy;
import com.fillumina.performance.util.TimeSpan;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducerTest {
    private static final long TIMEOUT = TimeSpan.set().min(1).asNanos();

    private static Configuration CONFIG = new Configuration() {
        @Override public long getTimeoutNanoseconds() { return TIMEOUT; }
        @Override public int getGarbageCollectorMillis() { return -1; }
        @Override public boolean getFilterSamples() { return false; }
        @Override public boolean getCoolDownCpu() { return false; }
    };

    private static class StrategyImpl implements Strategy {

        private int[] iterations;
        private int samples;

        public StrategyImpl iterations(final int[] value) {
            this.iterations = value;
            return this;
        }

        public StrategyImpl samples(final int value) {
            this.samples = value;
            return this;
        }

        @Override
        public int[] getIterations(PerformanceTimer pt) {
            return iterations;
        }

        @Override
        public int getSamples() {
            return samples;
        }

        @Override
        public boolean continueTakingSamples(SampleProgressionStatus status) {
            return status.getSample() < samples;
        }

        @Override
        public boolean repeatExecution(SpeedStats stats) {
            return false;
        }

        @Override
        public void onReset() {
            // do nothing
        }

        @Override
        public String getRejectionMessage() {
            return null;
        }
    }

    @Test(expected=IllegalStateException.class)
    public void shouldPeformanceExecutorBeNotNull() {
        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(CONFIG, new StrategyImpl());

        producer.execute();
    }

    @Test(expected=IllegalStateException.class)
    public void shouldTestsBePresent() {
        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(CONFIG, new StrategyImpl());

        producer.instrument(PerformanceTimerFactory.createSingleThreaded());

        producer.execute();
    }

    private class PerformanceTimerImpl extends PerformanceTimerMock {
        private int sampleCounter = 0;

        @Override
        public SpeedSample createFakePerformances(int[] iterations) {
            sampleCounter++;
            SpeedSampleMock.Builder builder = SpeedSampleMock.builder();
            int counter = 0;
            for (int i : iterations) {
                builder
                    .addTest("test_" + counter)
                    .iterations(iterations[counter])
                    .nansecondsPerOp(1)
                    .endTest();
                counter++;
            }
            return builder.createSample();
        }
    }

    @Test
    public void shouldPerformExecution() {
        Strategy strategy = new StrategyImpl()
                .iterations(new int[]{7, 5})
                .samples(13);

        PerformanceTimerImpl performanceTimer = new PerformanceTimerImpl();

        PHolder<SpeedStats> stats =
                executeWithStrategy(performanceTimer, strategy);

        assertEquals(13, performanceTimer.sampleCounter);

        SpeedStats speedStats = stats.getAssertable();

        //System.out.println(speedStats);

        Measure test0 = speedStats.getMeasure("test_0");
        assertEquals(13, test0.getCount(), 0);

        Measure test1 = speedStats.getMeasure("test_1");
        assertEquals(13, test1.getCount(), 0);
    }

    @Test
    public void shouldStopTakingSamples() {
        Strategy strategy = new StrategyImpl() {
                @Override
                public boolean continueTakingSamples(
                        SampleProgressionStatus status) {
                    return status.getSample() < 7;
                }
            }
            .iterations(new int[]{7, 5})
            .samples(13);

        PerformanceTimerImpl performanceTimer = new PerformanceTimerImpl();

        PHolder<SpeedStats> stats =
                executeWithStrategy(performanceTimer, strategy);

        SpeedStats speedStats = stats.getAssertable();

        Measure test0 = speedStats.getMeasure("test_0");
        assertEquals(7, test0.getCount(), 0);

        Measure test1 = speedStats.getMeasure("test_1");
        assertEquals(7, test1.getCount(), 0);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotAcceptZeroIterations() {
        Strategy strategy = new StrategyImpl()
                .iterations(new int[]{0, 10})
                .samples(13);

        PerformanceTimerImpl performanceTimer = new PerformanceTimerImpl();
        executeWithStrategy(performanceTimer, strategy);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotAcceptZeroSamples() {
        Strategy strategy = new StrategyImpl()
                .iterations(new int[]{10, 10})
                .samples(0);

        PerformanceTimerImpl performanceTimer = new PerformanceTimerImpl();
        executeWithStrategy(performanceTimer, strategy);
    }

    private PHolder<SpeedStats> executeWithStrategy(
            PerformanceTimer performanceTimer,
            Strategy strategy) {

        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(CONFIG, strategy);

        producer.instrument(performanceTimer);

        producer.addTest("aaaa", new RunnableMock());
        producer.addTest("bbbb", new RunnableMock());

        PHolder<SpeedStats> stats = producer.execute();
        return stats;
    }


    private class SampleListenerImpl implements SampleProgressionStatusListener {

        private SampleProgressionStatus status;

        @Override
        public void acceptSampleProgressionStatus(SampleProgressionStatus status) {
            this.status = status;
        }
    }

    @Test
    public void shuoldNotifyTheSampleProgressionStatus() {
        Strategy strategy = new StrategyImpl()
                .iterations(new int[]{7, 11})
                .samples(1);

        PerformanceTimerImpl performanceTimer = new PerformanceTimerImpl();

        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(CONFIG, strategy);

        producer.instrument(performanceTimer);

        SampleListenerImpl listener = new SampleListenerImpl();

        producer.addSampleProgressionListener(listener);

        producer.addTest("aaaa", new RunnableMock());

        producer.execute();

        assertEquals(7, listener.status.getIterations()[0], 0);
        assertEquals(11, listener.status.getIterations()[1], 0);
    }


    @Test
    public void shouldCallConsumer() {
        final PerformanceConsumerExecutionChecker<SpeedStats> consumer =
            new PerformanceConsumerExecutionChecker<>();

        new PerformanceTimerImpl()
                .instrumentedBy(RepeatingStatsProducerBuilder
                        .instance()
                        .setBaseIterations(10)
                        .setMaxPercentageMargin(Ratio.P_100)
                        .setCoolDownCpu(false)
                    .build())
                .addTest("example", new LfsrRunnable())
                .addPerformanceConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

}
