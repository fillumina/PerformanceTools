package com.fillumina.performance;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class AutoProgressionSerie {
    public static final int MINIMUM_ITERATIONS = 100;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionSequence progressionSerie;
    private double maxStandardDeviation = 1.5D;


    public AutoProgressionSerie(final PerformanceTimer pt) {
        this.progressionSerie = new ProgressionSequence(pt) {

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                return AutoProgressionSerie.this.stopIterating(serie);
            }

        };
    }

    public AutoProgressionSerie setPerformanceConsumerOnIteration(
            final PerformanceConsumer consumer) {
        progressionSerie.setOnIterationPerformanceConsumer(consumer);
        return this;
    }

    public AutoProgressionSerie setFinalPerformanceConsumer(
            final PerformanceConsumer consumer) {
        progressionSerie.setFinalPerformanceConsumer(consumer);
        return this;
    }

    public AutoProgressionSerie setTimeout(final int time, final TimeUnit unit) {
        progressionSerie.setTimeout(time, unit);
        return this;
    }

    public void serie(final int baseTimes,
            final int maximumMagnitude,
            final int samplePerMagnitude) {
        progressionSerie.serie(baseTimes, maximumMagnitude, samplePerMagnitude);
    }

    public AutoProgressionSerie getSeriePerformance() {
        progressionSerie.getSeriePerformance();
        return this;
    }

    public AutoProgressionSerie apply(PerformanceConsumer presenter) {
        progressionSerie.apply(presenter);
        return this;
    }

    public AutoProgressionSerie setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public void autoSerie() {
        serie(MINIMUM_ITERATIONS, MAXIMUM_MAGNITUDE, SAMPLE_PER_MAGNITUDE);
    }

    private boolean stopIterating(final SequencePerformances serie) {
        return serie.getMaximumStandardDeviation() < maxStandardDeviation;
    }
}
