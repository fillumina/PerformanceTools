package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;
import java.io.Serializable;

/**
 * Increments the iterations up to the point when the performances
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

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public AutoProgressionPerformanceInstrumenter(
            final AutoProgressionPerformanceInstrumenterBuilder builder) {
        final ProgressionPerformanceInstrumenterBuilder ppiBuilder =
                new ProgressionPerformanceInstrumenterBuilder();
        ppiBuilder.instrument(builder.getPerformanceTimer());
        ppiBuilder.setBaseAndMagnitude(10, 10);
        ppiBuilder.setSamplePerIterations(builder.getSamples());
        ppiBuilder.setTimeout(builder.getTimeout());

        this.progressionSerie = new ProgressionPerformanceInstrumenter(ppiBuilder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                final double stdDev = serie.calculateMaximumStandardDeviation();
                return stdDev < builder.getMaxStandardDeviation();
            }
        };
    }

    public AutoProgressionPerformanceInstrumenter(
            final ProgressionPerformanceInstrumenter progressionSerie) {
        this.progressionSerie = progressionSerie;
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
