package com.fillumina.performance.progression;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;

/**
 * Instrumenter that is instructed to execute the tests following a specified
 * progression of iterations. For general use you may consider
 * {@link com.fillumina.performance.progression.AutoProgressionPerformanceInstrumenter}
 * as a better alternative.
 * <p>
 * The JVM continuously optimizes the byte-code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and may
 * be triggered by different events such as configurations or the number of
 * executions of a particular piece of code.
 * If you measure the performance on a small amount of iterations
 * you may not capture the performances of the full optimized code. To better
 * understand the point from which the performances stabilize this class
 * runs tests incrementing the iterations number in successive steps.
 * <p>
 * The progression defines a sequence of {@code iterations} numbers each
 * of it will be executed a number of times defined by the {@code sample} value.
 * The performances reported by this {@link PerformanceExecutorInstrumenter}
 * are the average performances of all the samples in the step.
 * <p>
 * To execute its job this class needs to have assigned a
 * {@link InstrumentablePerformanceExecutor} via
 * {@link #instrument(InstrumentablePerformanceExecutor)}.
 * <p>
 * HINT: use the {@link AutoProgressionPerformanceInstrumenter} that is much
 * more robust.
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractPerformanceInstrumenter
                <ProgressionPerformanceInstrumenter> {

    private final int[] iterationsProgression;
    private final int samplesPerStep;
    private int progressionCounter;

    public static ProgressionPerformanceInstrumenterBuilder builder() {
        return new ProgressionPerformanceInstrumenterBuilder();
    }

    public ProgressionPerformanceInstrumenter(
            String message,
            long timeoutNanoseconds,
            long garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            int[] iterationsProgression,
            int samplesPerStep,
            PerformanceStatsConsumer[] performanceStatsConsumers) {
        super(message,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                performanceStatsConsumers);
        this.iterationsProgression = iterationsProgression;
        this.samplesPerStep = samplesPerStep;
    }

    @Override
    protected int getSamples() {
        return samplesPerStep;
    }

    @Override
    protected int getIterations() {
        final int iterations = iterationsProgression[progressionCounter];
        progressionCounter++;
        return iterations;
    }

    @Override
    protected boolean stopIterating(final PerformanceStats loopPerformances) {
        if (progressionCounter >= iterationsProgression.length) {
            progressionCounter = 0;
            return true;
        }
        return false;
    }

    @Override
    public String getMessage() {
        return "iteration = " + progressionCounter;
    }

}
