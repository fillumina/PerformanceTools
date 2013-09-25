package com.fillumina.performance.producer.progression;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * A builder that helps creating an
 * {@link AutoProgressionPerformanceInstrumenter} using
 * a <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
 * fluent interface</a></i>.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private double maxStandardDeviation = 1.5D;
    private long baseIterations = 1000;

    public AutoProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setSamplesPerStep(10);
        setTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        validate();
        if (maxStandardDeviation <= 0) {
            throw new IllegalArgumentException(
                    "maxStandardDeviation cannot be less than 0: " +
                    maxStandardDeviation);
        }
        return new AutoProgressionPerformanceInstrumenter(this);
    }

    /**
     * The maximum standard deviation allowed in the samples (each
     * sample consists in a a run of iterations ).
     * If the goals is not met the test will be repeated
     * with the number of iterations increased by an order of magnitude.
     * Reasonable values are between 0.4 and 10. If the value is too
     * low the sequence may not stabilize and the algorithm may
     * consequently not stop, if it is too high the results
     * may be grossly inaccurate. Bigger is the value faster will be the test.
     */
    public AutoProgressionPerformanceInstrumenterBuilder setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    protected double getMaxStandardDeviation() {
        return maxStandardDeviation;
    }

    /**
     * Set the starting number of iterations executed. This number will
     * be increased by an order of magnitude to reach the specified maximum
     * standard deviation.
     * Default value is 10.
     */
    public AutoProgressionPerformanceInstrumenterBuilder setBaseIterations(
            final long baseIterations) {
        this.baseIterations = baseIterations;
        return this;
    }

    protected long getBaseIterations() {
        return baseIterations;
    }
}
