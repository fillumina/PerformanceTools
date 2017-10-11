package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.progression.SampleProgressionStatusListener;
import com.fillumina.performance.executor.progression.StatsProgressionStatusListener;
import com.fillumina.performance.executor.sample.SampleProducer;
import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.filter.ListFilter;
import static com.fillumina.performance.util.filter.OutlierEliminatorFilter.DEFAULT_STANDARD_FACTOR;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.stats.Ratio;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration<C>
        extends CallBackBuilder<C, ProducerConfiguration>
        implements Activable {

    private boolean active = false;
    private int samples = 7;
    private double stdFilterFactor = DEFAULT_STANDARD_FACTOR;
    private boolean useMostOccurredFilter = true;
    private StringGenerator<MemStats> stringGenerator;
    private Ratio confidence = Ratio.P_99;

    public MemConfiguration() {
        super();
    }

    public MemConfiguration(C caller) {
        super(caller);
    }

    public MemConfiguration(Setter<C, ProducerConfiguration> setter) {
        super(setter);
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

    /**
     * Sets how many times from the mean a value must be to be considered an
     * outliers.
     */
    public MemConfiguration<C> setStdFilterFactor(final double value) {
        this.stdFilterFactor = value;
        return this;
    }

    public MemConfiguration<C> setConfidence(Ratio confidence) {
        this.confidence = confidence;
        return this;
    }


    public boolean isUseMostUsedFilter() {
        return useMostOccurredFilter;
    }

    /**
     * Use the most returned value only instead of a statistics.
     * (For memory is much more accurate if the results doesn't change).
     */
    public void setUseMostUsedFilter(boolean useMostOccurredFilter) {
        this.useMostOccurredFilter = useMostOccurredFilter;
    }

    public MemConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
    }

    Ratio getConfidence() {
        return confidence;
    }

    int getSamples() {
        return samples;
    }

    double getStdFilterFactor() {
        return stdFilterFactor;
    }

    public StringGenerator<MemStats> getStringGenerator() {
        return stringGenerator;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("stdFilterFactor", stdFilterFactor)
                .param("useMostOccurredFilter", useMostOccurredFilter)
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
            @Override
            public SampleProducer<?, ?> getSampleProducer() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int[] getIterations() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getWarmupSamples() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int getSamples() {
                return samples;
            }

            @Override
            public SampleProgressionStatusListener getSampleListener() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public StatsProgressionStatusListener getStatsListener() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ListFilter<Double> getSampleFilter() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            public long getTimeoutNanoseconds() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            public Ratio getMaxPercentageMargin() {
                return Ratio.decimal(1.0);
            }

            @Override
            public int getMillisecondsPerSample() {
                return -1;
            }

            @Override
            public int getConcurrencyLevel() {
                return 1;
            }

            @Override
            public int getWorkerNumber() {
                return 1;
            }

            @Override
            public long getSingleStatsTimeoutValue() {
                return 100L;
            }

            @Override
            public TimeUnit getSingleStatsTimeoutUnit() {
                return TimeUnit.DAYS;
            }
        };
    }
}
