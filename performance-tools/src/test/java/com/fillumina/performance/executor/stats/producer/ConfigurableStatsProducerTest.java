package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.mock.SampleMock;
import com.fillumina.performance.mock.SampleProducerMock;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.util.collection.ROIntList;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducerTest {

    private static class ConfigurationImpl
            implements ConfigurableStatsProducer.Configuration {
        private Quantity<IntervalUnit> timeout = IntervalUnit.MINUTES.quantity(1);
        private ListFilter<Double> filter = ListFilter.<Double>identity();
        private int garbageCollectorMillis = -1;
        private boolean coolDownCpu = false;

        @Override public Quantity<IntervalUnit> getStatsTimeout() {
            return timeout;
        }
        @Override public ListFilter<Double> getSampleFilter() {
            return filter;
        }
        @Override public int getGarbageCollectorMillis() {
            return garbageCollectorMillis;
        }
        @Override public boolean isCoolDownCpuActive() {
            return coolDownCpu;
        }
    }

    private static class StrategyImpl
            implements ConfigurableStatsProducer.Strategy {
        // attributes
        private ROIntList iterations;
        private int samples;
        private boolean repeatExecution;
        private String errorMessage;

        // passed values
        private SampleProgressionStatus status;
        private Collection<? extends Stats<?>> stats;
        private boolean onReset;

        @Override public ROIntList getIterations() {
            return iterations;
        }
        @Override public int getExpectedNumberOfSamples() {
            return samples;
        }
        @Override public boolean continueTakingSamples(SampleProgressionStatus status) {
            this.status = status;
            return status.getExecutedSamples() < samples;
        }
        @Override public boolean repeatExecution(
                Collection<? extends Stats<?>> stats) {
            this.stats = stats;
            return repeatExecution;
        }
        @Override public void onReset() {
            onReset = true;
        }
        @Override public String getErrorMessage() {
            return errorMessage;
        }
    }


    @Test(expected=IllegalStateException.class)
    public void shouldPeformanceExecutorBeNotNull() {
        ConfigurableStatsProducer<StatsMock,SampleMock> producer =
                new ConfigurableStatsProducer<>(
                        new ConfigurationImpl(), new StrategyImpl());

        producer.execute();
    }

    @Test(expected=IllegalStateException.class)
    public void shouldTestsBePresent() {
        ConfigurableStatsProducer<StatsMock,SampleMock> producer =
                new ConfigurableStatsProducer<>(
                        new ConfigurationImpl(), new StrategyImpl());

        SampleProducerMock sampleProducer = new SampleProducerMock();
        producer.instrument(sampleProducer);

        producer.execute();
    }

    @Test
    public void shouldPerformExecutionWithGivenSamples() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.iterations = new ROIntList(7,5);
        strategy.samples = 13;

        AssertableHolder<StatsMock> holder = executeWithStrategy(strategy);

        StatsMock stats = holder.getAssertable();

        Measure test0 = stats.getMeasure("test_0");
        assertEquals(13, test0.getCount(), 0);

        Measure test1 = stats.getMeasure("test_1");
        assertEquals(13, test1.getCount(), 0);
    }

    @Test
    public void shouldStopTakingSamples() {
        StrategyImpl strategy = new StrategyImpl() {
                @Override
                public boolean continueTakingSamples(
                        SampleProgressionStatus status) {
                    return status.getExecutedSamples() < 7;
                }
            };
        strategy.iterations = new ROIntList(7,5);
        strategy.samples = 13;

        AssertableHolder<StatsMock> stats =
                executeWithStrategy(strategy);

        StatsMock speedStats = stats.getAssertable();

        Measure test0 = speedStats.getMeasure("test_0");
        assertEquals(7, test0.getCount(), 0);

        Measure test1 = speedStats.getMeasure("test_1");
        assertEquals(7, test1.getCount(), 0);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotAcceptZeroSamples() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.iterations = new ROIntList(7, 10);
        strategy.samples = 0;

        executeWithStrategy(strategy);
    }

    private AssertableHolder<StatsMock> executeWithStrategy(
            ConfigurableStatsProducer.Strategy strategy) {

        ConfigurableStatsProducer<StatsMock,SampleMock> producer =
                new ConfigurableStatsProducer<>(new ConfigurationImpl(), strategy);

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples("test_0", 1.0)
                .addSamples("test_1", 2.0);

        producer.instrument(sampleProducer);

        producer.addTest("aaaa", new RunnableMock());
        producer.addTest("bbbb", new RunnableMock());

        AssertableHolder<StatsMock> stats =
                producer.execute().getStats(StatsMock.class);
        return stats;
    }
}
