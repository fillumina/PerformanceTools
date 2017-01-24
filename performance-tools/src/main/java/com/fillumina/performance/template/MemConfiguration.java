package com.fillumina.performance.template;

import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
import static com.fillumina.performance.util.filter.OutlierEliminatorFilter.DEFAULT_STANDARD_FACTOR;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration implements Activable {
    private final TestConfiguration testConfigurator;
    private final MemStatsTableStringGenerator stringGenerator;
    private boolean active = false;
    private int samples = 33;
    private double stdFilterFactor = DEFAULT_STANDARD_FACTOR;
    private boolean useMostUsedFilter = true;

    public MemConfiguration(TestConfiguration testConfigurator,
            MemStatsTableStringGenerator stringGenerator,
            int samples) {
        this.testConfigurator = testConfigurator;
        this.stringGenerator = stringGenerator;
        this.samples = samples;
    }

    /** Configures the speed test. */
    public SpeedConfiguration speedTest() {
        return testConfigurator.speedTest();
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     */
    public MemConfiguration usedMemTest() {
        return testConfigurator.usedMemTest();
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     */
    public MemConfiguration allocatedMemTest() {
        return testConfigurator.allocatedMemTest();
    }

    /** If true performs the memory test. */
    public MemConfiguration setActive(final boolean value) {
        this.active = value;
        return this;
    }

    /**
     * Sets how many times from the mean a value must be to be considered an
     * outliers.
     */
    public MemConfiguration setStdFilterFactor(final double value) {
        this.stdFilterFactor = value;
        return this;
    }

    public boolean isUseMostUsedFilter() {
        return useMostUsedFilter;
    }

    /**
     * Use the most returned value only instead of a statistics.
     * (For memory is much more accurate if the results doesn't change).
     */
    public void setUseMostUsedFilter(boolean useMostUsedFilter) {
        this.useMostUsedFilter = useMostUsedFilter;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    int getSamples() {
        return samples;
    }

    double getStdFilterFactor() {
        return stdFilterFactor;
    }

    MemStatsTableStringGenerator getStringGenerator() {
        return stringGenerator;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("stdFilterFactor", stdFilterFactor)
                .param("useMostUsedFilter", useMostUsedFilter)
                .toString();
    }
}
