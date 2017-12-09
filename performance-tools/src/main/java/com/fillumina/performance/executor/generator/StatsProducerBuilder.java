package com.fillumina.performance.executor.generator;

import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.template.ConsoleTimeProgressionListener;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerBuilder {

    private ConsoleTimeProgressionListener console;
    private Ratio confidence = Ratio.P_99;
    private boolean active = true;
    private SampleProgressionStatusListener sampleListener;
    private StatsProgressionStatusListener statsListener;
    private int concurrencyLevel = 1;

    private int workerNumber = 1;
    private Quantity<IntervalUnit> sampleTimeout = IntervalUnit.SECONDS.quantity(30);
    private Quantity<IntervalUnit> statsTimeout = IntervalUnit.MINUTES.quantity(3);
    private boolean consecutiveExecution = false;
    private int garbageCollectorMillis = -1;
    private boolean coolDownCpu = true;
    private ListFilter<Double> sampleFilter = OutlierEliminatorFilter.INSTANCE;
    private int warmupSamples = 0;
    private int samples = 33;
    private Ratio maxPercentageMargin = Ratio.P_05;
    private int[] iterations;

    public TestConfiguration<MixedStatsHolder> executeWithSampleProducer(
            SampleProducer<?> sampleProducer) {
        return new TestConfiguration<>(conf ->
            PerformanceGenerator.INSTANCE
                    .executeSingleTest(conf, new Configuration()) );
    }

    public StatsProducerBuilder console(
            final ConsoleTimeProgressionListener value) {
        this.console = value;
        return this;
    }

    public StatsProducerBuilder confidence(final Ratio value) {
        this.confidence = value;
        return this;
    }

    public StatsProducerBuilder active(final boolean value) {
        this.active = value;
        return this;
    }

    public StatsProducerBuilder sampleListener(
            final SampleProgressionStatusListener value) {
        this.sampleListener = value;
        return this;
    }

    public StatsProducerBuilder statsListener(
            final StatsProgressionStatusListener value) {
        this.statsListener = value;
        return this;
    }

    public StatsProducerBuilder concurrencyLevel(final int value) {
        this.concurrencyLevel = value;
        return this;
    }

    public StatsProducerBuilder workerNumber(final int value) {
        this.workerNumber = value;
        return this;
    }

    public StatsProducerBuilder sampleTimeout(final Quantity<IntervalUnit> value) {
        this.sampleTimeout = value;
        return this;
    }

    public StatsProducerBuilder statsTimeout(final Quantity<IntervalUnit> value) {
        this.statsTimeout = value;
        return this;
    }

    public StatsProducerBuilder consecutiveExecution(final boolean value) {
        this.consecutiveExecution = value;
        return this;
    }

    public StatsProducerBuilder garbageCollectorMillis(final int value) {
        this.garbageCollectorMillis = value;
        return this;
    }

    public StatsProducerBuilder coolDownCpu(final boolean value) {
        this.coolDownCpu = value;
        return this;
    }

    public StatsProducerBuilder sampleFilter(final ListFilter<Double> value) {
        this.sampleFilter = value;
        return this;
    }

    public StatsProducerBuilder warmupSamples(final int value) {
        this.warmupSamples = value;
        return this;
    }

    public StatsProducerBuilder samples(final int value) {
        this.samples = value;
        return this;
    }

    public StatsProducerBuilder maxPercentageMargin(final Ratio value) {
        this.maxPercentageMargin = value;
        return this;
    }

    public StatsProducerBuilder iterations(final int[] value) {
        this.iterations = value;
        return this;
    }



    public class Configuration implements ProducerConfiguration {
        @Override
        public Ratio getConfidence() {
            return confidence;
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
    }

}
