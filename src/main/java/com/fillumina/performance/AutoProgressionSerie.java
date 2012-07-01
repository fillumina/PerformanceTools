package com.fillumina.performance;

/**
 *
 * @author fra
 */
public class AutoProgressionSerie {
    public static final int MINIMUM_ITERATIONS = 100;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionSerie progressionSerie;
    private double maxStandardDeviation = 1.5D;


    public AutoProgressionSerie(final PerformanceTimer pt) {
        this.progressionSerie = new ProgressionSerie(pt) {

            @Override
            protected boolean stopIterating(SeriePerformances serie) {
                return AutoProgressionSerie.this.stopIterating(serie);
            }

        };
    }

    public AutoProgressionSerie setPerformanceConsumerOnIteration(
            final PerformanceConsumer consumer) {
        progressionSerie.setPerformanceConsumerOnIteration(consumer);
        return this;
    }

    public AutoProgressionSerie setFinalPerformanceConsumer(
            final PerformanceConsumer consumer) {
        progressionSerie.setFinalPerformanceConsumer(consumer);
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

    private boolean stopIterating(final SeriePerformances serie) {
        return serie.getMaximumStandardDeviation() < maxStandardDeviation;
    }
}
