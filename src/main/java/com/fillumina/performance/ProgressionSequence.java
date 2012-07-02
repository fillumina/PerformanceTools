package com.fillumina.performance;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class ProgressionSequence {
    private final SequencePerformances serie = new SequencePerformances();
    private final PerformanceTimer pt;
    private PerformanceConsumer iterationConsumer;
    private PerformanceConsumer finalConsumer;
    private long timeout = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS);

    public ProgressionSequence(final PerformanceTimer pt) {
        this.pt = pt;
    }

    public ProgressionSequence apply(final PerformanceConsumer presenter) {
        this.iterationConsumer = presenter;
        return this;
    }

    /**
     * The assigned consumer receives all the intermediate
     * test sequence results.
     */
    public ProgressionSequence setOnIterationPerformanceConsumer(
            PerformanceConsumer consumer) {
        this.iterationConsumer = consumer;
        return this;
    }

    /**
     * The assigned consumer receives the average of the
     * values of the last sequence of tests.
     */
    public ProgressionSequence setFinalPerformanceConsumer(
            PerformanceConsumer consumer) {
        this.finalConsumer = consumer;
        return this;
    }

    public ProgressionSequence setTimeout(final int time, final TimeUnit unit) {
        timeout = unit.toNanos(time);
        return this;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final SequencePerformances serie) {
        return false;
    }

    public void serie(final int baseTimes,
            final int maximumMagnitude,
            final int samplePerMagnitude) {
        long start = System.nanoTime();
        int index = 0;
        for (int magnitude=0; magnitude< maximumMagnitude; magnitude++) {
            for (int iteration=0; iteration<samplePerMagnitude; iteration++) {
                final long loops = Math.round(baseTimes * Math.pow(10, magnitude));
                pt.iterate((int)loops);
                pt.use(serie);
                iterationConsumer();
                pt.clear();
                index++;
                if (System.nanoTime() - start > timeout) {
                    throw new RuntimeException("Timeout");
                }
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
            pt.use(iterationConsumer).consume();
        }
    }

    private void finalConsumer() {
        if (finalConsumer != null) {
            finalConsumer.setPerformances(serie.getAverageLoopPerformances());
            finalConsumer.consume();
        }
    }

    public SequencePerformances getSeriePerformance() {
        return serie;
    }
}
