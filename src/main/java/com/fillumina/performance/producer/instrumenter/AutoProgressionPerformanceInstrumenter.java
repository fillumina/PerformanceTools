package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Increments the iterations up to the point where the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iterations. It
 * produces accurate measures but may be very long to execute.
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenter
        implements Serializable,
        PerformanceInstrumenter<AutoProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final ProgressionPerformanceInstrumenter progressionSerie;

    public static class Builder
            extends AbstractIstrumenterBuilder<Builder,
                    AutoProgressionPerformanceInstrumenter>
            implements Serializable {
        private static final long serialVersionUID = 1L;

        private double maxStandardDeviation = 1.5D;

        public Builder() {
            super();

            // init with default values
            setBaseAndMagnitude(1_000, 8);
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
        public Builder setMaxStandardDeviation(final double maxStandardDeviation) {
            this.maxStandardDeviation = maxStandardDeviation;
            return this;
        }
    }

    public AutoProgressionPerformanceInstrumenter(final Builder builder) {
        this.progressionSerie = new ProgressionPerformanceInstrumenter(builder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                return serie.calculateMaximumStandardDeviation() <
                        builder.maxStandardDeviation;
            }
        };
    }

    @Override
    public AutoProgressionPerformanceInstrumenter
            addPerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionSerie.addPerformanceConsumer(consumers);
        return this;
    }

    @Override
    public LoopPerformancesHolder executeSequence() {
        return progressionSerie.executeSequence();
    }

}
