package com.fillumina.performance.template;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.ConsumerAggregator;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.ConvergenceFilter;
import com.fillumina.performance.util.filter.FilterChain;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class SpeedConfiguration<C>
        extends CallBackBuilder<C, MixedProducerConfiguration>
        implements Activable {

    private final  ConsumerAggregator<Sample> sampleConsumer =
            new ConsumerAggregator<>();

    private final ConsumerAggregator<Stats> statsConsumer =
            new ConsumerAggregator<>();

    private int concurrencyLevel = 1;
    private int workerNumber = 1;
    private Quantity<IntervalUnit> sampleTimeout = IntervalUnit.SECONDS.quantity(120);
    private Quantity<IntervalUnit> statsTimeout = IntervalUnit.HOURS.quantity(1);
    private boolean consecutiveExecution = false;
    private int garbageCollectorMillis = -1;
    private ListFilter<Double> sampleFilter = new FilterChain<>(33,
            OutlierEliminatorFilter.INSTANCE,
            ConvergenceFilter.INSTANCE);
    private boolean coolDownCpu = true;
    private int warmupSamples = 0;
    private int samples = 33;
    private int millisecondsPerSample = 250;
    private Ratio maxPercentageMargin = Ratio.percentage(5.0);
    private int[] iterations;
    private SampleProgressionStatusListener sampleListener = null;
    private StatsProgressionStatusListener statsListener = null;
    private StringGenerator<Stats> averageTimeStatsStringGenerator =
            TimeStatsStringGeneratorSelector.AVERAGE_TIME;
    private StringGenerator<Stats> throughputStatsStringGenerator =
            TimeStatsStringGeneratorSelector.THROUGHPUT;

    private boolean active = false;
    private Ratio confidence = Ratio.P_99;

    public SpeedConfiguration() {
        super();
    }

    public SpeedConfiguration(C caller) {
        super(caller);
    }

    public SpeedConfiguration(Setter<C, MixedProducerConfiguration> setter) {
        super(setter);
    }

    protected SpeedConfiguration<C> setPerformanceSampleConsumer(
            Consumer<Sample> sampleConsumer) {
        this.sampleConsumer.add(sampleConsumer);
        return this;
    }

    /** Sets a statistics consumer. */
    public SpeedConfiguration<C> setPerformanceStatsConsumer(
            Consumer<Stats> statsPerformanceConsumer) {
        this.statsConsumer.add(statsPerformanceConsumer);
        return this;
    }

    /** Sets speed test. */
    public SpeedConfiguration<C> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /** Sets threads and workers. */
    public SpeedConfiguration<C> setParallelTasks(final int tasks) {
        setConcurrencyLevel(tasks);
        setWorkerNumber(tasks);
        return this;
    }

    /** Sets as many threads and workers as available CPUs. */
    public SpeedConfiguration<C> setMultiThreading(final boolean parallel) {
        int cpus = parallel ? Runtime.getRuntime().availableProcessors() : 1;
        setConcurrencyLevel(cpus);
        setWorkerNumber(cpus);
        return this;
    }

    /** How many thread will be available. */
    public SpeedConfiguration<C> setConcurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public SpeedConfiguration<C> setWorkerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    public SpeedConfiguration<C> setSampleTimeout(Quantity<IntervalUnit> timeout) {
        this.sampleTimeout = timeout;
        return this;
    }

    public SpeedConfiguration<C> setStatsTimeout(Quantity<IntervalUnit> timeout) {
        this.statsTimeout = timeout;
        return this;
    }

    public SpeedConfiguration<C> setConsecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public SpeedConfiguration<C> setGarbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public SpeedConfiguration<C> setFilterSamples(
            final ListFilter<Double> sampleFilter) {
        this.sampleFilter = sampleFilter;
        return this;
    }

    public SpeedConfiguration<C> setCoolDownCpu(final boolean value) {
        this.coolDownCpu = value;
        return this;
    }

    public SpeedConfiguration<C> setIterations(final int... value) {
        this.iterations = value;
        return this;
    }

    public SpeedConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public SpeedConfiguration<C> setWarmupSamples(final int value) {
        this.warmupSamples = value;
        return this;
    }

    public SpeedConfiguration<C> setMaxPercentageMargin(final Ratio value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public SpeedConfiguration<C> setMillisecondsPerSample(final int value) {
        this.millisecondsPerSample = value;
        return this;
    }

    public SpeedConfiguration<C> setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return this;
    }

    public SpeedConfiguration<C> setSampleListener(
            SampleProgressionStatusListener sampleListener) {
        this.sampleListener = sampleListener;
        return this;
    }

    public SpeedConfiguration<C> setStatsListener(
            StatsProgressionStatusListener statsListener) {
        this.statsListener = statsListener;
        return this;
    }

    public SpeedConfiguration<C> setAverageTimeStatsStringGenerator(
            final StringGenerator<Stats> value) {
        this.averageTimeStatsStringGenerator = value;
        return this;
    }

    public SpeedConfiguration<C> setThroughputStatsStringGenerator(
            final StringGenerator<Stats> value) {
        this.throughputStatsStringGenerator = value;
        return this;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("concurrencyLevel", concurrencyLevel)
                .param("workerNumber", workerNumber)
                .param("timeout", sampleTimeout)
                .param("consecutiveExecution", consecutiveExecution)
                .param("garbageCollectorMillis", garbageCollectorMillis, -1, "no")
                .param("coolDownCpu", coolDownCpu)
                .param("samples", samples)
                .param("iterations", Arrays.toString(iterations), "null", "auto")
                .param("millisecondsPerSample", millisecondsPerSample)
                .param("filterSamples", sampleFilter)
                .param("maxPercentageMargin", maxPercentageMargin)
                .param("confidence", confidence.toString())
                .toString();
    }

    @Override
    public MixedProducerConfiguration build() {
        return new MixedProducerConfiguration() {

            private ConsoleTimeProgressionListener console;

            @Override
            public void setVerbosity(Verbosity verbosity) {
                console = new ConsoleTimeProgressionListener(verbosity,
                        confidence);
            }

            @Override
            public Ratio getConfidence() {
                return confidence;
            }

            @Override
            public Map<Stats.Type, StringGenerator<Stats>>
                    getStringGenerators() {
                return LinkedMap.create(TimeStatsType.AVERAGE,
                        averageTimeStatsStringGenerator,
                        TimeStatsType.THROUGHPUT,
                        throughputStatsStringGenerator
                );
            }

            @Override
            public SampleProducer<?> getSampleProducer() {
                return PerformanceTimerFactory.createPerformanceTimer(this);
            }

            @Override
            public boolean isActive() {
                return active;
            }

            @Override
            public SampleProgressionStatusListener getSampleListener() {
                if (sampleListener == null) {
                    return console;
                }
                return sampleListener;
            }

            @Override
            public StatsProgressionStatusListener getStatsListener() {
                if (statsListener == null) {
                    return console;
                }
                return statsListener;
            }

            @Override
            public int getConcurrencyLevel() {
                return concurrencyLevel;
            }

            @Override
            public int getWorkerNumber() {
                return workerNumber;
            }

            @Override
            public Quantity<IntervalUnit> getSampleTimeout() {
                return sampleTimeout;
            }

            @Override
            public Quantity<IntervalUnit> getStatsTimeout() {
                return statsTimeout;
            }

            @Override
            public boolean isConsecutiveExecution() {
                return consecutiveExecution;
            }

            @Override
            public int getGarbageCollectorMillis() {
                return garbageCollectorMillis;
            }

            @Override
            public ListFilter<Double> getSampleFilter() {
                return sampleFilter;
            }

            @Override
            public boolean isCoolDownCpuActive() {
                return coolDownCpu;
            }

            @Override
            public int getWarmupSamples() {
                return warmupSamples;
            }

            @Override
            public int getSamples() {
                return samples;
            }

            @Override
            public Ratio getMaxAllowedMargin() {
                return maxPercentageMargin;
            }

            @Override
            public int[] getIterations() {
                return iterations;
            }
        };
    }
}
