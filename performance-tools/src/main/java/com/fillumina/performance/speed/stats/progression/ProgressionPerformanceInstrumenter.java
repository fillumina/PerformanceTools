package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.stats.Ratio;

/**
 * Calculates the performance of tests executed a fixed number of times.
 * <p>
 * The JVM continuously optimizes the running code at runtime based on the live
 * statistics it collects. This process takes place in multiple steps that
 * depend on JVM type, architecture and configuration.
 * This class is useful if you are interested at the performances of a test
 * in some of its stage of optimization. It allows to defines a fixed
 * number of iterations and provides statistics about them.
 * <p>
 * The progression defines a sequence of {@code iterations} values each
 * of them will be executed a number of times defined by the {@code sample} value.
 * The performances reported are the average performances of
 * the last iteration step executed.
 *
 * @author Francesco Illuminati
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractPerformanceInstrumenter
                <ProgressionPerformanceInstrumenter> {

    private final int[] iterationsProgression;
    private final int samples;
    private int progressionCounter;

    public static ProgressionPerformanceInstrumenterBuilder builder() {
        return new ProgressionPerformanceInstrumenterBuilder();
    }

    public ProgressionPerformanceInstrumenter(
            StaticPath name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            Ratio confidence,
            boolean eliminateOutliers,
            int[] iterationsProgression,
            int samples,
            PerformanceConsumer<SpeedStats>[] performanceStatsConsumers) {
        super(name,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
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
        return createIterationArray(iterations);
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
