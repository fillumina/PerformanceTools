package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.progression.ConfigurableStatsProducer;
import com.fillumina.performance.executor.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.executor.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.executor.progression.MatchRequiredMarginStrategy;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.time.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.ConsumerAggregator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class ProducerConfiguration<C>
        extends CallBackBuilder<C, ProducerConfiguration<C>>
        implements
            Activable,
            SelectorMultiThreadPerformanceExecutor.Configuration,
            FixedSamplesAndIterationsStrategy.Configuration,
            MatchRequiredMarginStrategy.Configuration,
            ConsecutiveExecutorStatsProducer.Configuration,
            ConfigurableStatsProducer.Configuration {

    private final  ConsumerAggregator<AbstractSample<?,?,?>> sampleConsumer =
            new ConsumerAggregator<>();

    private final ConsumerAggregator<Stats<?>> statsConsumer =
            new ConsumerAggregator<>();

    private boolean active = false;
    private Ratio confidence = Ratio.P_999;

    public ProducerConfiguration() {
        super();
    }

    public ProducerConfiguration(C caller) {
        super(caller);
    }

    public ProducerConfiguration(Setter<C, ProducerConfiguration<C>> setter) {
        super(setter);
    }

    /** Sets speed test. */
    public ProducerConfiguration<C> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    protected ProducerConfiguration<C> setPerformanceSampleConsumer(
            Consumer<AbstractSample<?,?,?>> sampleConsumer) {
        this.sampleConsumer.add(sampleConsumer);
        return this;
    }

    /** Sets a statistics consumer. */
    public ProducerConfiguration<C> setPerformanceStatsConsumer(
            Consumer<Stats<?>> statsPerformanceConsumer) {
        this.statsConsumer.add(statsPerformanceConsumer);
        return this;
    }

    private int concurrencyLevel = 1;
    private int workerNumber = 1;
    private long timeoutValue = 120;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;
    private boolean consecutiveExecution = false;
    private int garbageCollectorMillis = -1;
    private ListFilter<Double> sampleFilter = null;
    private boolean coolDownCpu = true;
    private int warmupSamples = 0;
    private int samples = 33;
    private int millisecondsPerSample = 250;
    private Ratio maxPercentageMargin = Ratio.percentage(5.0);
    private int[] iterations;

    /** Sets threads and workers. */
    public ProducerConfiguration<C> setParallelTasks(final int tasks) {
        setConcurrencyLevel(tasks);
        setWorkerNumber(tasks);
        return this;
    }

    /** Sets as many threads and workers as available CPUs. */
    public ProducerConfiguration<C> setMultiThreading(final boolean parallel) {
        int cpus = parallel ? Runtime.getRuntime().availableProcessors() : 1;
        setConcurrencyLevel(cpus);
        setWorkerNumber(cpus);
        return this;
    }

    /** How many thread will be available. */
    public ProducerConfiguration<C> setConcurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public ProducerConfiguration<C> setWorkerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    public ProducerConfiguration<C> setTimeout(long time, TimeUnit unit) {
        setTimeoutValue(time);
        setTimeoutUnit(unit);
        return this;
    }

    public ProducerConfiguration<C> setTimeoutValue(final long value) {
        this.timeoutValue = value;
        return this;
    }

    public ProducerConfiguration<C> setTimeoutUnit(final TimeUnit value) {
        this.timeoutUnit = value;
        return this;
    }

    public ProducerConfiguration<C> setConsecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public ProducerConfiguration<C> setGarbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public ProducerConfiguration<C> setFilterSamples(
            final ListFilter<Double> sampleFilter) {
        this.sampleFilter = sampleFilter;
        return this;
    }

    public ProducerConfiguration<C> setCoolDownCpu(final boolean value) {
        this.coolDownCpu = value;
        return this;
    }

    public ProducerConfiguration<C> setIterations(final int... value) {
        this.iterations = value;
        return this;
    }

    public ProducerConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public ProducerConfiguration<C> setWarmupSamples(final int value) {
        this.warmupSamples = value;
        return this;
    }

    public ProducerConfiguration<C> setMaxPercentageMargin(final Ratio value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public ProducerConfiguration<C> setMillisecondsPerSample(final int value) {
        this.millisecondsPerSample = value;
        return this;
    }

    public ProducerConfiguration<C> setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return this;
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
    public long getTimeoutValue() {
        return timeoutValue;
    }

    @Override
    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    @Override
    public boolean isConsecutiveExecution() {
        return consecutiveExecution;
    }

    @Override
    public long getTimeoutNanoseconds() {
        return TimeUnit.NANOSECONDS.convert(timeoutValue, timeoutUnit);
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
    public boolean getCoolDownCpu() {
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
    public Ratio getMaxPercentageMargin() {
        return maxPercentageMargin;
    }

    @Override
    public int getMillisecondsPerSample() {
        return millisecondsPerSample;
    }

    @Override
    public int[] getIterations() {
        return iterations;
    }

    @Override
    public ProducerConfiguration<C> build() {
        return this;
    }

    public Ratio getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("concurrencyLevel", concurrencyLevel)
                .param("workerNumber", workerNumber)
                .param("timeout", timeoutValue + " " + timeoutUnit.toString())
                .param("consecutiveExecution", consecutiveExecution)
                .param("garbageCollectorMillis", garbageCollectorMillis)
                .param("coolDownCpu", coolDownCpu)
                .param("samples", samples)
                .param("iterations", Arrays.toString(iterations))
                .param("millisecondsPerSample", millisecondsPerSample)
                .param("filterSamples", sampleFilter)
                .param("maxPercentageMargin", maxPercentageMargin)
                .param("confidence", confidence.toString())
                .toString();
    }
}
