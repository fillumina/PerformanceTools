package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.FluentPerformanceProducer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.PerformanceProducerInstrumenter;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Increments the iterations up to the point where the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iteration. It
 * produces accurate measures but may be very long to execute.
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenter
        implements FluentPerformanceProducer, PerformanceProducerInstrumenter,
        Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MINIMUM_ITERATIONS = 1000;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionPerformanceInstrumenter progressionSerie;
    private double maxStandardDeviation = 1.5D;

    public AutoProgressionPerformanceInstrumenter(final PerformanceTimer pt) {
        this.progressionSerie = new ProgressionPerformanceInstrumenter() {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                return AutoProgressionPerformanceInstrumenter.this
                        .stopIterating(serie);
            }
        };
    }

    /**
     * The assigned consumer receives the average of the
     * values of the last sequence of tests.
     */
    @Override
    public AutoProgressionPerformanceInstrumenter setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        progressionSerie.setPerformanceConsumer(consumer);
        return this;
    }

    public AutoProgressionPerformanceInstrumenter setTimeout(final int time,
            final TimeUnit unit) {
        progressionSerie.setTimeout(time, unit);
        return this;
    }

    public AutoProgressionPerformanceInstrumenter getSeriePerformance() {
        progressionSerie.getSeriePerformance();
        return this;
    }

    @Override
    public <T extends PerformanceConsumer> T use(final T consumer) {
        return progressionSerie.use(consumer);
    }

    /** Reasonable values are between 0.4 and 1.5 */
    public AutoProgressionPerformanceInstrumenter setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public FluentPerformanceProducer executeAutoSequence() {
        return progressionSerie
                .setBaseAndMagnitude(MINIMUM_ITERATIONS, MAXIMUM_MAGNITUDE)
                .setSamplePerIterations(SAMPLE_PER_MAGNITUDE)
                .executeSequence();
    }

    private boolean stopIterating(final SequencePerformances serie) {
        return serie.getMaximumStandardDeviation() < maxStandardDeviation;
    }

    @Override
    public void setPerformanceTimer(final PerformanceTimer performanceTimer) {
        progressionSerie.setPerformanceTimer(performanceTimer);
    }
}
