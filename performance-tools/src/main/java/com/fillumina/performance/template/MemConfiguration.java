package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.generator.Verbosity;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration<C>
        extends CallBackBuilder<C, ProducerConfiguration>
        implements Activable {

    private static final Quantity<IntervalUnit> TIMEOUT =
            IntervalUnit.DAYS.quantity(1);

    private final SampleProducer<?, ?> sampleProducer;
    private final String description;

    private boolean active = false;
    private int samples = 7;
    private StringGenerator<MemStats> stringGenerator =
            MemStatsTableStringGenerator.USED_INSTANCE;
    private Ratio confidence = Ratio.P_99;
    private ListFilter<Double> sampleFilter = OutlierEliminatorFilter.INSTANCE;
    private SampleProgressionStatusListener sampleListener = null;
    private StatsProgressionStatusListener statsListener = null;
    private Verbosity verbosity;

    public MemConfiguration(SampleProducer<?, ?> sampleProducer,
            String description) {
        this.sampleProducer = sampleProducer;
        this.description = description;
    }

    public MemConfiguration(C caller,
            SampleProducer<?, ?> sampleProducer,
            String description) {
        super(caller);
        this.sampleProducer = sampleProducer;
        this.description = description;
    }

    public MemConfiguration(
            Setter<C, ProducerConfiguration> setter,
            SampleProducer<?, ?> sampleProducer,
            String description) {
        super(setter);
        this.sampleProducer = sampleProducer;
        this.description = description;
    }

    public MemConfiguration<C> setStringGenerator(
            StringGenerator<MemStats> stringGenerator) {
        this.stringGenerator = stringGenerator;
        return this;
    }

    /** If true performs the memory test. */
    public MemConfiguration<C> setActive(final boolean value) {
        this.active = value;
        return this;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public MemConfiguration<C> setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return this;
    }

    public MemConfiguration<C> setSampleFilter(ListFilter<Double> sampleFilter) {
        this.sampleFilter = sampleFilter;
        return this;
    }

    public MemConfiguration<C> setSampleListener(
            SampleProgressionStatusListener sampleListener) {
        this.sampleListener = sampleListener;
        return this;
    }

    public MemConfiguration<C> setStatsListener(
            StatsProgressionStatusListener statsListener) {
        this.statsListener = statsListener;
        return this;
    }

    public MemConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public MemConfiguration<C> setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
        return this;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("confidence", confidence.toString())
                .emptyLine()
                .emptyLine()
                .line("ALERT:")
                .line("Memory estimation is accurate until a certain amount only")
                .line("(about 250 KiB) depending on current JVM and memory")
                .line("manager. If you need an accuracy estimation use")
                .line("MemoryAllocatorInfo.INSTANCE.calculateMemoryAccuracyThreshold(null).")
                .toString();
    }

    @Override
    public ProducerConfiguration build() {
        return new ProducerConfiguration() {

            private final ConsoleMemProgressionListener console =
                    new ConsoleMemProgressionListener(
                            verbosity == null ? Verbosity.FULL_OUTPUT : verbosity,
                            confidence,
                            description);

            @Override
            public SampleProducer<?, ?> getSampleProducer() {
                return sampleProducer;
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
            public ListFilter<Double> getSampleFilter() {
                return sampleFilter;
            }

            @Override
            public int[] getIterations() {
                return new int[]{1}; // TODO is that right?
            }

            @Override
            public int getWarmupSamples() {
                return 0; // TODO could it be more?
            }

            @Override
            public int getSamples() {
                return samples;
            }

            @Override
            public Quantity<IntervalUnit> getStatsTimeout() {
                return TIMEOUT;
            }

            @Override
            public Quantity<IntervalUnit> getSampleTimeout() {
                return TIMEOUT;
            }

            @Override
            public boolean isActive() {
                return active;
            }

            @Override
            public boolean isConsecutiveExecution() {
                return true; // TODO would try non consecutive?
            }

            @Override
            public int getGarbageCollectorMillis() {
                return -1;
            }

            @Override
            public boolean getCoolDownCpu() {
                return false;
            }

            @Override
            public Ratio getMaxAllowedMargin() {
                return Ratio.decimal(1.0);
            }

            @Override
            public int getConcurrencyLevel() {
                return 1;
            }

            @Override
            public int getWorkerNumber() {
                return 1;
            }
        };
    }
}
