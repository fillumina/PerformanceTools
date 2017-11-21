package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.AbstractStatsProducerTest;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsTableStringGenerator;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.RunnableMock;
import com.fillumina.performance.mock.SampleProducerMock;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConfigurableStatsProducerTest
        extends AbstractStatsProducerTest {
    private static final boolean OUTPUT = false;

    private static final String TEST_0 = "test_0";
    private static final String TEST_1 = "test_1";

    @Override
    public ConfigurableStatsProducer createStatsProducer() {
        ConfigurationImpl conf = new ConfigurationImpl();
        StrategyImpl strategy = new StrategyImpl();
        SampleProducerMock sampleProducer = new SampleProducerMock() {
            @Override
            public Map<Stats.Type, Sample> get() {
                for (Runnable r : getTests().values()) {
                    r.run();
                }
                return super.get();
            }
        }.addSamples(TEST_0, 1.0, 3.0);

        return createProducer(conf, strategy, sampleProducer);
    }

    private static class ConfigurationImpl
            implements ConfigurableStatsProducer.Configuration {
        private Quantity<IntervalUnit> timeout = IntervalUnit.MINUTES.quantity(1);
        private ListFilter<Double> filter = ListFilter.<Double>identity();
        private int garbageCollectorMillis = -1;
        private boolean coolDownCpu = OUTPUT;

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
        private UnmodifiableIntList iterations = UnmodifiableIntList.EMPTY;
        private int samples = 1;
        private boolean repeatExecution = OUTPUT;
        private String errorMessage;

        @Override public UnmodifiableIntList getIterations() {
            return iterations;
        }
        @Override public int getExpectedNumberOfSamples() {
            return samples;
        }
        @Override public boolean continueTakingSamples(
                SampleProgressionStatus status) {
            return status.getExecutedSamples() < samples;
        }
        @Override public boolean repeatExecution(
                Collection<Stats> stats) {
            return repeatExecution;
        }
        @Override public String getStatusMessage() {
            return errorMessage;
        }
    }

    @Test(expected=IllegalStateException.class)
    public void shouldPeformanceExecutorBeNotNull() {
        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(
                        new ConfigurationImpl(), new StrategyImpl());

        producer.execute();
    }

    @Test(expected=IllegalStateException.class)
    public void shouldTestsBePresent() {
        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(
                        new ConfigurationImpl(), new StrategyImpl());

        SampleProducerMock sampleProducer = new SampleProducerMock();
        producer.instrument(sampleProducer);

        producer.execute();
    }

    @Test(expected=RuntimeException.class)
    public void shouldTimeout() {
        ConfigurationImpl conf = new ConfigurationImpl();
        conf.timeout = IntervalUnit.NANOSECONDS.quantity(1);

        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 33;

        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(conf, strategy);

        producer.instrument(new SampleProducerMock());

        producer.addTest(() -> {});

        producer.execute();
    }

    @Test
    public void shouldPerformExecutionWithGivenSamples() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 13;

        StatsHolder holder = execute(new ConfigurationImpl(), strategy);

        Stats stats = holder.getStats();

        Measure test0 = stats.getMeasure(TEST_0);
        assertEquals(13, test0.getCount(), 0);

        Measure test1 = stats.getMeasure(TEST_1);
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
        strategy.samples = 13;

        StatsHolder holder = execute(new ConfigurationImpl(), strategy);

        Stats stats = holder.getStats();

        Measure test0 = stats.getMeasure(TEST_0);
        assertEquals(7, test0.getCount(), 0);

        Measure test1 = stats.getMeasure(TEST_1);
        assertEquals(7, test1.getCount(), 0);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldNotAcceptZeroSamples() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 0;

        execute(new ConfigurationImpl(), strategy);
    }

    @Test
    public void shouldFilterSamples() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 2;

        List<Double> filteredValues = new ArrayList<>();

        ConfigurationImpl config = new ConfigurationImpl();
        config.filter = new ListFilter<Double>() {
            @Override
            public <T> List<T> filter(List<T> list,
                    Function<T, Double> extractor) {
                list.stream().forEach(
                        t -> filteredValues.add(extractor.apply(t)) ) ;
                return list;
            }
        };

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 3.0)
                .addSamples(TEST_1, 2.0, 4.0);

        execute(config, strategy, sampleProducer);

        assertEquals(Arrays.asList(1.0, 2.0, 1.0, 3.0, 2.0, 4.0),
                filteredValues);
    }

    @Test
    public void shouldExecuteWithTheGivenSamples() {
        checkSamples(1, 1.0);
        checkSamples(2, (1.0 + 10.0) / 2.0);
        checkSamples(3, (1.0 + 10.0 + 100.0) / 3.0);
        checkSamples(4, (1.0 + 10.0 + 100.0 + 1_000.0) / 4.0);
        checkSamples(5, (1.0 + 10.0 + 100.0 + 1_000.0 + 10_000.0) / 5.0);
    }

    private void checkSamples(int samples, double mean) {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = samples;

        // defines the AVAILABLE samples
        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 10.0, 100.0, 1_000.0, 10_000.0);

        StatsHolder holder =
                execute(new ConfigurationImpl(), strategy, sampleProducer);

        Stats stats = holder.getStats();
        Measure test0 = stats.getMeasure(TEST_0);
        String errMsg = "samples=" + samples + ", mean=" + mean +
                System.lineSeparator() + stats.toString();

        assertEquals(errMsg, mean, test0.getMean(), 0);
    }

    @Test
    public void shouldReturnTheRightStats() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        StatsHolder holder =
                execute(new ConfigurationImpl(), strategy, sampleProducer);

        Stats stats = holder.getStats();

        Measure test0 = stats.getMeasure(TEST_0);
        assertEquals(5.0, test0.getMean(), 1E-8);

        Measure test1 = stats.getMeasure(TEST_1);
        assertEquals(0.5, test1.getMean(), 1E-8);
    }

    @Test
    public void shouldPassStatusWhenCallingStrategyContinueTakingSamples() {
        Holder.Integer index = new Holder.Integer(0);
        StrategyImpl strategy = new StrategyImpl() {
            @Override
            public boolean continueTakingSamples(SampleProgressionStatus status) {
                assertEquals(index.incrementAndGet(), status.getExecutedSamples());
                assertEquals(0, status.getRepetitions());
                assertNull(status.getStatusMessage());
                return super.continueTakingSamples(status);
            }
        };
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        execute(new ConfigurationImpl(), strategy, sampleProducer);

        assertEquals(9, index.getValue(), 0);
    }

    @Test
    public void shouldPassStatsWhenCallingStrategyRepeatExecution() {
        Holder<Collection<Stats>> coll = new Holder<>();
        StrategyImpl strategy = new StrategyImpl() {
            @Override
            public boolean repeatExecution(Collection<Stats> stats) {
                assertNotNull(stats);
                coll.setValue(stats);
                return super.repeatExecution(stats);
            }
        };
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        StatsHolder holder =
                execute(new ConfigurationImpl(), strategy, sampleProducer);

        Stats stats = holder.getStats();

        assertEquals(1, coll.getValue().size());
        assertEquals(stats, coll.getValue().iterator().next());
    }

    @Test
    public void shouldRepeatTest() {
        Holder.Boolean repeatCalled = new Holder.Boolean(false);
        StrategyImpl strategy = new StrategyImpl() {
            @Override
            public boolean repeatExecution(Collection<Stats> stats) {
                if (repeatCalled.getValue()) {
                    return false;
                }
                repeatCalled.setValue(true);
                return true;
            }
        };
        strategy.samples = 1;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0)
                .addSamples(TEST_1, 0.1, 0.2);

        StatsHolder holder =
                execute(new ConfigurationImpl(), strategy, sampleProducer);

        Stats stats = holder.getStats();

        assertTrue(repeatCalled.getValue());
        assertEquals(2.0, stats.getMeasure(TEST_0).getMean(), 0);
        assertEquals(0.2, stats.getMeasure(TEST_1).getMean(), 0);
    }

    @Test
    public void shouldReportStatusMessage() {
        final String statusMessage = "xyz";
        StrategyImpl strategy = new StrategyImpl() {
            @Override
            public String getStatusMessage() {
                return statusMessage;
            }
        };
        strategy.samples = 1;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0);

        ConfigurableStatsProducer producer =
                createProducer(new ConfigurationImpl(), strategy, sampleProducer);

        Holder.Boolean executed = new Holder.Boolean(false);
        producer.addSampleProgressionListener(
                (SampleProgressionStatus s) -> {
                    assertEquals(statusMessage, s.getStatusMessage());
                    executed.setValue(true);
                });

        producer.execute();

        assertTrue(executed.getValue());
    }

    @Test
    public void shouldReturnSampleStatus() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        ConfigurableStatsProducer producer =
                createProducer(new ConfigurationImpl(), strategy, sampleProducer);

        Holder.Integer index = new Holder.Integer(0);

        producer.addSampleProgressionListener(
                (SampleProgressionStatus status) -> {
                    assertEquals(index.incrementAndGet(),
                            status.getExecutedSamples());
                    assertEquals(0, status.getRepetitions());
                    assertNull(status.getStatusMessage());
                });

        producer.execute();

        assertEquals(9, index.getValue(), 0);
    }

    @Test
    public void shouldReturnStatsStatus() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        ConfigurableStatsProducer producer =
                createProducer(new ConfigurationImpl(), strategy, sampleProducer);

        Holder<Collection<Stats>> coll = new Holder<>();

        producer.addStatsProgressionListener(
                (StatsProgressionStatus status) ->
                        coll.setValue(status.getStats()));

        StatsHolder holder = producer.execute().getHolder(MockStatsType.INSTANCE);

        Stats stats = holder.getStats();

        assertEquals(1, coll.getValue().size());
        assertEquals(stats, coll.getValue().iterator().next());
    }

    @Test
    public void shouldCallStatsConsumer() {
        StrategyImpl strategy = new StrategyImpl();
        strategy.samples = 9;

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                .addSamples(TEST_1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);

        ConfigurableStatsProducer producer =
                createProducer(new ConfigurationImpl(), strategy, sampleProducer);

        Holder<Stats> consumed = new Holder<>();

        producer.addConsumer( t -> consumed.setValue(t) );

        StatsHolder holder =
                producer.execute().getHolder(MockStatsType.INSTANCE);

        Stats stats = holder.getStats();

        assertEquals(stats, consumed.getValue());
    }

    private StatsHolder execute(
            ConfigurableStatsProducer.Configuration config,
            ConfigurableStatsProducer.Strategy strategy) {

        SampleProducerMock sampleProducer = new SampleProducerMock()
                .addSamples(TEST_0, 1.0)
                .addSamples(TEST_1, 2.0);

        return execute(config, strategy, sampleProducer);
    }

    private StatsHolder execute(
            ConfigurableStatsProducer.Configuration config,
            ConfigurableStatsProducer.Strategy strategy,
            SampleProducerMock sampleProducer) {

        ConfigurableStatsProducer producer =
                createProducer(config, strategy, sampleProducer);

        return producer.execute().getHolder(MockStatsType.INSTANCE);
    }

    private ConfigurableStatsProducer createProducer(
            ConfigurableStatsProducer.Configuration config,
            ConfigurableStatsProducer.Strategy strategy,
            SampleProducerMock sampleProducer) {
        ConfigurableStatsProducer producer =
                new ConfigurableStatsProducer(config, strategy);

        producer.instrument(sampleProducer);

        for (SampleProducerMock.Samples s : sampleProducer.getSamples()) {
            producer.addTest(s.getName(), new RunnableMock());
        }

        if (OUTPUT) {
            sampleProducer.addConsumer(SampleLineStringGenerator.VIEWER);
            producer.addConsumer(StatsTableStringGenerator.VIEWER);
        }

        return producer;
    }
}
