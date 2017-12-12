package com.fillumina.performance.util.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReciprocalOnlineMeasureSampler {
    private final OnlineMeasure direct = new OnlineMeasure();
    private final OnlineMeasure inverse = new OnlineMeasure();

    public ReciprocalOnlineMeasureSampler addAll(final double... values) {
        for (double value: values) {
            addSample(value);
        }
        return this;
    }

    public ReciprocalOnlineMeasureSampler addAll(
            final Iterable<? extends Number> collection) {
        for (Number value: collection) {
            addSample(value.doubleValue());
        }
        return this;
    }

    public ReciprocalOnlineMeasureSampler addSample(double value) {
        direct.addSample(value);
        inverse.addSample(1.0/value);
        return this;
    }

    public ReciprocalOnlineMeasureSampler clear() {
        direct.clear();
        inverse.clear();
        return this;
    }

    public OnlineMeasure getDirect() {
        return direct;
    }

    public OnlineMeasure getInverse() {
        return inverse;
    }
}
