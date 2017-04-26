package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TreeName;

/**
 * Calculates the performance of tests executed a fixed number of times.
 * <p>
 * The JVM continuously optimizes the running code based on live
 * statistics it collects. This process takes place in multiple steps that
 * depend on JVM type, architecture and configuration.
 * With this instrumenter you can control exactly for how many iterations
 * a specific code should be executed.
 * <p>
 * The progression defines a sequence of {@code iterations} values each
 * of them will be executed a number of times defined by the {@code sample} value.
 * The performances reported are the average performances of
 * the last iteration step executed.
 *
 * @author Francesco Illuminati
 */
public class ProgressionStatsProducer
        extends AbstractProgressionStatsProducer
                <ProgressionStatsProducer> {

    private final int[] iterationsProgression;
    private final int samples;
    private int progressionCounter;

    public static ProgressionStatsProducerBuilder builder() {
        return new ProgressionStatsProducerBuilder();
    }

    public ProgressionStatsProducer(
            TreeName name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            boolean filterSamples,
            boolean coolDownCpu,
            int[] iterationsProgression,
            int samples,
            PerformanceConsumer<SpeedStats>[] performanceStatsConsumers) {
        super(name,
                timeoutNanoseconds,
                garbageCollectorMillis,
                filterSamples,
                coolDownCpu,
                performanceStatsConsumers);
        this.iterationsProgression = iterationsProgression;
        this.samples = samples;
    }

    @Override
    protected int getSamples() {
        return samples;
    }

    @Override
    protected int[] getIterations() {
        final int iterations = iterationsProgression[progressionCounter];
        progressionCounter++;
        return createIterationsArray(iterations);
    }

    @Override
    protected boolean repeatExecution(final SpeedStats loopPerformances) {
        if (progressionCounter >= iterationsProgression.length) {
            progressionCounter = 0;
            return false;
        }
        return true;
    }

    @Override
    protected String getRejectionMessage() {
        return "iteration = " + progressionCounter;
    }

}
