package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.producer.FluentPerformanceProducer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Increments the iterations up to the point where the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iteration. It
 * produces accurate measures but may be very long to execute.
 *
 *
 * @author fra
 */
public class AutoProgressionSequence
        implements FluentPerformanceProducer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final int MINIMUM_ITERATIONS = 1000;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionSequence progressionSerie;
    private double maxStandardDeviation = 1.5D;

    public AutoProgressionSequence(final PerformanceTimer pt) {
        this.progressionSerie = new ProgressionSequence(pt) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                return AutoProgressionSequence.this.stopIterating(serie);
            }

        };
    }

    public AutoProgressionSequence setPerformanceConsumerOnIteration(
            final PerformanceConsumer consumer) {
        progressionSerie.setOnIterationPerformanceConsumer(consumer);
        return this;
    }

    /**
     * The assigned consumer receives the average of the
     * values of the last sequence of tests.
     */
    @Override
    public AutoProgressionSequence setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        progressionSerie.setPerformanceConsumer(consumer);
        return this;
    }

    public AutoProgressionSequence setTimeout(final int time, final TimeUnit unit) {
        progressionSerie.setTimeout(time, unit);
        return this;
    }

    public AutoProgressionSequence getSeriePerformance() {
        progressionSerie.getSeriePerformance();
        return this;
    }

    @Override
    public <T extends PerformanceConsumer> T use(final T consumer) {
        return progressionSerie.use(consumer);
    }

    /** Reasonable values are between 0.4 and 1.5 */
    public AutoProgressionSequence setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public FluentPerformanceProducer executeAutoSequence() {
        return progressionSerie
                .setBaseTimes(MINIMUM_ITERATIONS)
                .setMaximumMagnitude(MAXIMUM_MAGNITUDE)
                .setSamplePerMagnitude(SAMPLE_PER_MAGNITUDE)
                .executeSequence();
    }

    private boolean stopIterating(final SequencePerformances serie) {
        return serie.getMaximumStandardDeviation() < maxStandardDeviation;
    }
}
