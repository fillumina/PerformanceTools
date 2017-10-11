package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.ConsumerAggregator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class ProducerConfigurationImpl<C>
        extends CallBackBuilder<C, ProducerConfigurationImpl<C>>
        implements ProducerConfiguration {

    private final  ConsumerAggregator<AbstractSample<?,?,?>> sampleConsumer =
            new ConsumerAggregator<>();

    private final ConsumerAggregator<Stats<?>> statsConsumer =
            new ConsumerAggregator<>();

    private final Function<ProducerConfiguration,SampleProducer<?, ?>>
            sampleProducer;

    private int concurrencyLevel = 1;
    private int workerNumber = 1;
    private Quantity<IntervalUnit> timeoutValue = IntervalUnit.SECONDS.quantity(120);
    private boolean consecutiveExecution = false;
    private int garbageCollectorMillis = -1;
    private ListFilter<Double> sampleFilter = null;
    private boolean coolDownCpu = true;
    private int warmupSamples = 0;
    private int samples = 33;
    private int millisecondsPerSample = 250;
    private Ratio maxPercentageMargin = Ratio.percentage(5.0);
    private int[] iterations;
    private SampleProgressionStatusListener sampleListener =
            SampleProgressionStatusListener.NULL;
    private StatsProgressionStatusListener statsListener =
            StatsProgressionStatusListener.NULL;


    private boolean active = false;
    private Ratio confidence = Ratio.P_999;

    public ProducerConfigurationImpl(
            Function<ProducerConfiguration,SampleProducer<?, ?>> sampleProducer) {
        this.sampleProducer = sampleProducer;
    }

    public ProducerConfigurationImpl(
            Function<ProducerConfiguration,SampleProducer<?, ?>> sampleProducer,
            C caller) {
        super(caller);
        this.sampleProducer = sampleProducer;
    }

    public ProducerConfigurationImpl(
            Function<ProducerConfiguration,SampleProducer<?, ?>> sampleProducer,
            Setter<C, ProducerConfigurationImpl<C>> setter) {
        super(setter);
        this.sampleProducer = sampleProducer;
    }

    protected ProducerConfigurationImpl<C> setPerformanceSampleConsumer(
            Consumer<AbstractSample<?,?,?>> sampleConsumer) {
        this.sampleConsumer.add(sampleConsumer);
        return this;
    }

    /** Sets a statistics consumer. */
    public ProducerConfigurationImpl<C> setPerformanceStatsConsumer(
            Consumer<Stats<?>> statsPerformanceConsumer) {
        this.statsConsumer.add(statsPerformanceConsumer);
        return this;
    }

    /** Sets speed test. */
    public ProducerConfigurationImpl<C> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /** Sets threads and workers. */
    public ProducerConfigurationImpl<C> setParallelTasks(final int tasks) {
        setConcurrencyLevel(tasks);
        setWorkerNumber(tasks);
        return this;
    }

    /** Sets as many threads and workers as available CPUs. */
    public ProducerConfigurationImpl<C> setMultiThreading(final boolean parallel) {
        int cpus = parallel ? Runtime.getRuntime().availableProcessors() : 1;
        setConcurrencyLevel(cpus);
        setWorkerNumber(cpus);
        return this;
    }

    /** How many thread will be available. */
    public ProducerConfigurationImpl<C> setConcurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setWorkerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setTimeout(Quantity<IntervalUnit> timeoutValue) {
        this.timeoutValue = timeoutValue;
        return this;
    }

    public ProducerConfigurationImpl<C> setConsecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setGarbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setFilterSamples(
            final ListFilter<Double> sampleFilter) {
        this.sampleFilter = sampleFilter;
        return this;
    }

    public ProducerConfigurationImpl<C> setCoolDownCpu(final boolean value) {
        this.coolDownCpu = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setIterations(final int... value) {
        this.iterations = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setWarmupSamples(final int value) {
        this.warmupSamples = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setMaxPercentageMargin(final Ratio value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setMillisecondsPerSample(final int value) {
        this.millisecondsPerSample = value;
        return this;
    }

    public ProducerConfigurationImpl<C> setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return this;
    }

    public void setSampleListener(SampleProgressionStatusListener sampleListener) {
        this.sampleListener = sampleListener;
    }

    public void setStatsListener(StatsProgressionStatusListener statsListener) {
        this.statsListener = statsListener;
    }

    @Override
    public SampleProgressionStatusListener getSampleListener() {
        return sampleListener;
    }

    @Override
    public StatsProgressionStatusListener getStatsListener() {
        return statsListener;
    }

    @Override
    public SampleProducer<?, ?> getSampleProducer() {
        return sampleProducer.apply(this);
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
    public Quantity<IntervalUnit> getSingleStatsTimeoutValue() {
        return timeoutValue;
    }

    @Override
    public boolean isConsecutiveExecution() {
        return consecutiveExecution;
    }

    @Override
    public long getTimeoutNanoseconds() {
        return (long) timeoutValue.as(IntervalUnit.NANOSECONDS);
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
    public ProducerConfigurationImpl<C> build() {
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
