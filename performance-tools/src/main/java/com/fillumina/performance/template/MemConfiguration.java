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

    public MemConfiguration(TestConfiguration testConfigurator,
            MemStatsTableStringGenerator stringGenerator,
            int samples) {
        this.testConfigurator = testConfigurator;
        this.stringGenerator = stringGenerator;
        this.samples = samples;
    }

    public SpeedConfiguration speedTest() {
        return testConfigurator.speedTest();
    }

    public MemConfiguration usedMemTest() {
        return testConfigurator.usedMemTest();
    }

    public MemConfiguration allocatedMemTest() {
        return testConfigurator.allocatedMemTest();
    }

    public MemConfiguration setActive(final boolean value) {
        this.active = value;
        return this;
    }

    public MemConfiguration setSamples(final int value) {
        this.samples = value;
        return this;
    }

    public MemConfiguration setStdFilterFactor(final double value) {
        this.stdFilterFactor = value;
        return this;
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
                .toString();
    }
}
