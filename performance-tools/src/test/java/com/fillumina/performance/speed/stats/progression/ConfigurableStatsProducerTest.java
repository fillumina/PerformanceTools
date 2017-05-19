package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumerExecutionChecker;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.PerformanceTimerMock;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimerFactory;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer.Configuration;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer.Strategy;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducerTest {

    private static Configuration CONFIG = new Configuration() {
        @Override public TName getName() { return TN.tname("test"); }
        @Override public long getTimeoutNanoseconds() { return 100_000_000_000L; }
        @Override public int getGarbageCollectorMillis() { return -1; }
        @Override public boolean getFilterSamples() { return false; }
        @Override public boolean getCoolDownCpu() { return false; }
        @Override public PerformanceConsumer<SpeedStats> getStatsConsumers() {
            return null;
        }
    };

    private static class StrategyImpl implements Strategy {
        private int[] iterations = {10};
        private int[] samples = {10};
        private int sampleCounter;
        private int takingSamples = 1_000_000;
        private boolean repeatExecution = false;
        private String rejectionMessage = "rejected";
        private boolean reset = false;

        public StrategyImpl iterations(final int[] value) {
            this.iterations = value;
            return this;
        }

        public StrategyImpl samples(final int... values) {
            this.samples = values;
            return this;
        }

        public StrategyImpl takingSamples(final int value) {
            this.takingSamples = value;
            return this;
        }

        public StrategyImpl repeatExecution(final boolean value) {
            this.repeatExecution = value;
            return this;
        }

        public StrategyImpl rejectionMessage(final String value) {
            this.rejectionMessage = value;
            return this;
        }

        public StrategyImpl reset(final boolean value) {
            this.reset = value;
            return this;
        }

        @Override
        public int[] getIterations(PerformanceTimer pt) {
            return iterations;
        }

        @Override
        public int getSamples() {
            int index = sampleCounter;
            sampleCounter++;
            return samples[index];
        }

        @Override
        public boolean continueTakingSamples(SampleProgressionStatus status) {
            takingSamples--;
            return takingSamples > 0;
        }

        @Override
        public boolean repeatExecution(SpeedStats stats) {
            return repeatExecution;
        }

        @Override
        public void onReset() {
            reset = true;
        }

        @Override
        public String getRejectionMessage() {
            return rejectionMessage;
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
        Strategy strategy = new StrategyImpl()
                .iterations(new int[]{7, 5})
                .samples(13)
                .takingSamples(7);  // <--------------------(())

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
                .samples(1)
                .repeatExecution(false);

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
                        .setMaxPercentageMargin(100)
                        .setCoolDownCpu(false)
                    .build())
                .addTest("example", new LfsrRunnable())
                .addPerformanceConsumer(consumer)
                .execute();

        assertTrue(consumer.isNotified());
    }

}
