package com.fillumina.performance;

/**
 *
 * @author fra
 */
public class ProgressionSerie {
    private final SeriePerformances serie = new SeriePerformances();
    private final PerformanceTimer pt;
    private PerformanceConsumer iterationConsumer;
    private PerformanceConsumer finalConsumer;

    public ProgressionSerie(final PerformanceTimer pt) {
        this.pt = pt;
    }

    public ProgressionSerie apply(final PerformanceConsumer presenter) {
        this.iterationConsumer = presenter;
        return this;
    }

    public ProgressionSerie setPerformanceConsumerOnIteration(
            PerformanceConsumer consumer) {
        this.iterationConsumer = consumer;
        return this;
    }

    public ProgressionSerie setFinalPerformanceConsumer(
            PerformanceConsumer consumer) {
        this.finalConsumer = consumer;
        return this;
    }

    protected boolean stopIterating(final SeriePerformances serie) {
        return false;
    }

    public void serie(final int baseTimes,
            final int maximumMagnitude,
            final int samplePerMagnitude) {
        int index = 0;
        for (int magnitude=0; magnitude< maximumMagnitude; magnitude++) {
            for (int iteration=0; iteration<samplePerMagnitude; iteration++) {
                final long loops = Math.round(baseTimes * Math.pow(10, magnitude));
                pt.iterate((int)loops);
                pt.apply(serie);
                iterationConsumer();
                pt.clear();
                index++;
            }
            if (stopIterating(serie)) {
                break;
            }
            if (magnitude != maximumMagnitude - 1) {
                serie.clear();
            }
        }
        finalConsumer();
    }

    private void iterationConsumer() {
        if (iterationConsumer != null) {
            pt.apply(iterationConsumer).consume();
        }
    }

    private void finalConsumer() {
        if (finalConsumer != null) {
            finalConsumer.setPerformances(serie.getAverageLoopPerformances());
            finalConsumer.consume();
        }
    }

    public SeriePerformances getSeriePerformance() {
        return serie;
    }
}
