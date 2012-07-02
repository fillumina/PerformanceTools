package com.fillumina.performance.sequence;

import com.fillumina.performance.PerformanceProducer;
import com.fillumina.performance.PerformanceConsumer;
import com.fillumina.performance.timer.PerformanceTimer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class AutoProgressionSequence implements PerformanceProducer {
    public static final int MINIMUM_ITERATIONS = 100;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionSequence progressionSerie;
    private double maxStandardDeviation = 1.5D;


    public AutoProgressionSequence(final PerformanceTimer pt) {
        this.progressionSerie = new ProgressionSequence(pt) {

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
        progressionSerie.apply(consumer);
        return consumer;
    }

    public AutoProgressionSequence setMaxStandardDeviation(
            final double maxStandardDeviation) {
        this.maxStandardDeviation = maxStandardDeviation;
        return this;
    }

    public void autoSerie() {
        progressionSerie
                .serie(MINIMUM_ITERATIONS, MAXIMUM_MAGNITUDE, SAMPLE_PER_MAGNITUDE);
    }

    private boolean stopIterating(final SequencePerformances serie) {
        return serie.getMaximumStandardDeviation() < maxStandardDeviation;
    }
}
