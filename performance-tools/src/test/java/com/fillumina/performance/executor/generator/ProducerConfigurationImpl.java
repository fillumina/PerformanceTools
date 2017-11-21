package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProducerConfigurationImpl implements ProducerConfiguration {

    private final SampleProducer<?> sampleProducer;

    private Ratio confidence = Ratio.P_999;
    private SampleProgressionStatusListener sampleListener =
            SampleProgressionStatusListener.NULL;
    private StatsProgressionStatusListener statsListener =
            StatsProgressionStatusListener.NULL;
    private boolean active = true;
    private Quantity<IntervalUnit> statsTimeout = IntervalUnit.HOURS.quantity(1);
    private Quantity<IntervalUnit> sampleTimeout = IntervalUnit.HOURS.quantity(1);
    private ListFilter<Double> sampleFilter = ListFilter.<Double>identity();
    private int garbageCollectorMillis = -1;
    private boolean coolDownCpuActive = false;
    private boolean consecutiveExecution = false;
    private int[] iterations;
    private int warmupSamples = 0;
    private int samples = 33;
    private Ratio maxAllowedMargin = Ratio.percentage(5);
    private int concurrencyLevel = 0;
    private int workerNumber = 0;

    public ProducerConfigurationImpl(SampleProducer<?> sampleProducer) {
        this.sampleProducer = sampleProducer;
    }

    public ProducerConfigurationImpl sampleListener(
            final SampleProgressionStatusListener value) {
        this.sampleListener = value;
        return this;
    }

    public ProducerConfigurationImpl statsListener(
            final StatsProgressionStatusListener value) {
        this.statsListener = value;
        return this;
    }

    public ProducerConfigurationImpl active(final boolean value) {
        this.active = value;
        return this;
    }

    public ProducerConfigurationImpl statsTimeout(
            final Quantity<IntervalUnit> value) {
        this.statsTimeout = value;
        return this;
    }

    public ProducerConfigurationImpl sampleTimeout(
            final Quantity<IntervalUnit> value) {
        this.sampleTimeout = value;
        return this;
    }

    public ProducerConfigurationImpl sampleFilter(final ListFilter<Double> value) {
        this.sampleFilter = value;
        return this;
    }

    public ProducerConfigurationImpl garbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public ProducerConfigurationImpl coolDownCpuActive(final boolean value) {
        this.coolDownCpuActive = value;
        return this;
    }

    public ProducerConfigurationImpl consecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public ProducerConfigurationImpl iterations(final int[] value) {
        this.iterations = value;
        return this;
    }

    public ProducerConfigurationImpl warmupSamples(final int value) {
        this.warmupSamples = value;
        return this;
    }

    public ProducerConfigurationImpl samples(final int value) {
        this.samples = value;
        return this;
    }

    public ProducerConfigurationImpl maxAllowedMargin(final Ratio value) {
        this.maxAllowedMargin = value;
        return this;
    }

    public ProducerConfigurationImpl concurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public ProducerConfigurationImpl workerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    @Override
    public Ratio getConfidence() {
        return confidence;
    }

    @Override
    public SampleProducer<?> getSampleProducer() {
        return sampleProducer;
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
    public boolean isActive() {
        return active;
    }

    @Override
    public Quantity<IntervalUnit> getStatsTimeout() {
        return statsTimeout;
    }

    @Override
    public ListFilter<Double> getSampleFilter() {
        return sampleFilter;
    }

    @Override
    public int getGarbageCollectorMillis() {
        return garbageCollectorMillis;
    }

    @Override
    public boolean isCoolDownCpuActive() {
        return coolDownCpuActive;
    }

    @Override
    public boolean isConsecutiveExecution() {
        return consecutiveExecution;
    }

    @Override
    public int[] getIterations() {
        return iterations;
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
        return maxAllowedMargin;
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
}
