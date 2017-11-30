package com.fillumina.performance.template;

import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.producer.SampleProgressionStatusListener;
import com.fillumina.performance.executor.stats.producer.StatsProgressionStatusListener;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.filter.FilterListSizeSelector;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration<C>
        extends CallBackBuilder<C, MixedProducerConfiguration>
        implements Activable {

    private static final Quantity<IntervalUnit> TIMEOUT =
            IntervalUnit.DAYS.quantity(1);

    private final SampleProducer<?> sampleProducer;
    private final String description;
    private final Stats.Type type;

    private boolean active = false;
    private int samples = 7;
    private StringGenerator<Stats> stringGenerator =
            MemStatsTableStringGenerator.INSTANCE;
    private Ratio confidence = Ratio.P_99;
    private ListFilter<Double> sampleFilter = new FilterListSizeSelector<>(
            OutlierEliminatorFilter.INSTANCE,
            MostUsedFilter.<Double>instance(),
            size -> size >= 33);
    private SampleProgressionStatusListener sampleListener = null;
    private StatsProgressionStatusListener statsListener = null;

    public MemConfiguration(SampleProducer<?> sampleProducer,
            String description,
            Stats.Type type) {
        this.sampleProducer = sampleProducer;
        this.description = description;
        this.type = type;
    }

    public MemConfiguration(C caller,
            SampleProducer<?> sampleProducer,
            String description,
            Stats.Type type) {
        super(caller);
        this.sampleProducer = sampleProducer;
        this.description = description;
        this.type = type;
    }

    public MemConfiguration(
            Setter<C, MixedProducerConfiguration> setter,
            SampleProducer<?> sampleProducer,
            String description,
            Stats.Type type) {
        super(setter);
        this.sampleProducer = sampleProducer;
        this.description = description;
        this.type = type;
    }

    public MemConfiguration<C> setStringGenerator(
            StringGenerator<Stats> stringGenerator) {
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

    @Override
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("confidence", confidence.toString())
                .emptyLine()
                .emptyLine()
                .text(80, "WARNING:",
                    "Memory estimation is accurate until a certain amount only",
                    "(about 250 KiB) depending on current JVM and memory",
                    "manager. If you need an accuracy estimation use:")
                .line("MemoryAllocatorInfo.INSTANCE.calculateMemoryAccuracyThreshold(null).")
                .toString();
    }

    @Override
    public MixedProducerConfiguration build() {
        return new MixedProducerConfiguration() {

            private ConsoleMemProgressionListener console;

            @Override
            public void setVerbosity(Verbosity verbosity) {
                console = new ConsoleMemProgressionListener(verbosity,
                        confidence, description);
            }

            @Override
            public Ratio getConfidence() {
                return confidence;
            }

            @Override
            public Map<Stats.Type, StringGenerator<Stats>> getStringGenerators() {
                return ArrayMap.create(type, stringGenerator);
            }

            @Override
            public SampleProducer<?> getSampleProducer() {
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
                return 1;
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
                return false; // TODO would try non consecutive?
            }

            @Override
            public int getGarbageCollectorMillis() {
                return -1;
            }

            @Override
            public boolean isCoolDownCpuActive() {
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
