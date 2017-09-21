package com.fillumina.performance.executor.progression;

import com.fillumina.performance.executor.stats.Stats;
import java.util.Collection;

/**
 * Calculates the performance of tests executed a fixed number of times.
 * <p>
 * The progression defines a sequence of {@code iterations} values each
 * of them will be executed a number of times defined by the {@code sample} value.
 * The performances reported are the average performances of
 * the last iteration step executed.
 *
 * @author Francesco Illuminati
 */
public class FixedSamplesAndIterationsStrategy
        implements ConfigurableStatsProducer.Strategy {

    public interface Configuration {
        int[] getIterations();
        int getSamples();
    }

    private final int[] iterationsProgression;
    private final int samples;
    private int progressionCounter;

    public FixedSamplesAndIterationsStrategy(Configuration config) {
        this.iterationsProgression = config.getIterations();
        this.samples = config.getSamples();
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public int[] getIterations() {
        final int iterations = iterationsProgression[progressionCounter];
        progressionCounter++;
        return new int[]{iterations};
    }

    @Override
    public boolean repeatExecution(final Collection<? extends Stats<?>> stats) {
        if (progressionCounter >= iterationsProgression.length) {
            progressionCounter = 0;
            return false;
        }
        return true;
    }

    @Override
    public String getRejectionMessage() {
        return "iteration = " + progressionCounter;
    }

    @Override
    public boolean continueTakingSamples(SampleProgressionStatus status) {
        return status.getExecutedSamples() < samples;
    }

    @Override
    public void onReset() {
        progressionCounter = 0;
    }
}
