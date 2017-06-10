package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import static com.fillumina.performance.util.filter.OutlierEliminatorFilter.DEFAULT_STANDARD_FACTOR;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration<C>
        extends CallBackBuilder<C, MemConfiguration<C>>
        implements Activable {

    private boolean active = false;
    private int samples = 7;
    private double stdFilterFactor = DEFAULT_STANDARD_FACTOR;
    private boolean useMostOccurredFilter = true;
    private StringGenerator<MemStats> stringGenerator;

    public MemConfiguration() {
    }

    public MemConfiguration(C caller) {
        super(caller);
    }

    public MemConfiguration(Setter<C, MemConfiguration<C>> setter) {
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

    @Override
    public boolean isActive() {
        return active;
    }

    public MemConfiguration<C> setSamples(final int value) {
        this.samples = value;
        return this;
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
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("stdFilterFactor", stdFilterFactor)
                .param("useMostOccurredFilter", useMostOccurredFilter)
                .emptyLine()
                .toString() +
            new TableFormatter()
                .line("ALERT:")
                .line("Memory estimation is accurate until a certain amount only")
                .line("(about 250 KiB) depending on current JVM and memory")
                .line("manager. If you need an accuracy estimation please use")
                .line("MemoryAllocatorInfo.INSTANCE.calculateMemoryAccuracyThreshold(null).");
    }

    @Override
    public MemConfiguration<C> build() {
        return this;
    }
}
