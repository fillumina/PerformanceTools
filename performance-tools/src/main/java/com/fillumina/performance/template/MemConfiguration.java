package com.fillumina.performance.template;

import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.TableFormatter;
import static com.fillumina.performance.util.filter.OutlierEliminatorFilter.DEFAULT_STANDARD_FACTOR;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemConfiguration implements Activable {
    private final TestConfiguration testConfigurator;
    private boolean active = false;
    private int samples = 33;
    private double stdFilterFactor = DEFAULT_STANDARD_FACTOR;

    public MemConfiguration(TestConfiguration testConfigurator) {
        this.testConfigurator = testConfigurator;
    }

    public TestConfiguration endMemConfig() {
        return testConfigurator;
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

    @Override
    public String toString() {
        return new TableFormatter()
                .param("samples", samples)
                .param("stdFilterFactor", stdFilterFactor)
                .toString();
    }
}
