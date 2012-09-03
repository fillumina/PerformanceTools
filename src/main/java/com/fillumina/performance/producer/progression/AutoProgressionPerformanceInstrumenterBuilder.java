package com.fillumina.performance.producer.progression;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenterBuilder
        extends AbstractIstrumenterBuilder<
            AutoProgressionPerformanceInstrumenterBuilder,
            AutoProgressionPerformanceInstrumenter>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private double maxStandardDeviation = 1.5D;

    public AutoProgressionPerformanceInstrumenterBuilder() {
        super();
        // init with default values
        setSamplePerIterations(10);
        setTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public AutoProgressionPerformanceInstrumenter build() {
        check();
        if (maxStandardDeviation <= 0) {
            throw new IllegalArgumentException(
                    "maxStandardDeviation cannot be less than 0: " +
                    maxStandardDeviation);
        }
        return new AutoProgressionPerformanceInstrumenter(this);
    }

    /**
     * Reasonable values are between 0.4 and 1.5. If the value is too
     * low the sequence may not stabilize and the algorithm may
     * consequently not stop, if it is too high the results
     * may be grossly inaccurate.
     */
    public AutoProgressionPerformanceInstrumenterBuilder setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public double getMaxStandardDeviation() {
        return maxStandardDeviation;
    }
}
