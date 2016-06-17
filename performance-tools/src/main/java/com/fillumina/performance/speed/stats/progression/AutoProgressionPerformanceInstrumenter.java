package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import java.util.Arrays;

/**
 * Automatically finds the optimal parameters to perform a performance
 * estimation of the code under test. It increases the number of iterations
 * or samples (according to configuration) so to meet the minimum accuracy
 * requirements.
 * <p>
 * It produces statistics based on the average results of the last round of
 * iterations.
 *
 * @author Francesco Illuminati
 */
public class AutoProgressionPerformanceInstrumenter
        extends AbstractPerformanceInstrumenter
            <AutoProgressionPerformanceInstrumenter> {

    private final boolean incrementIteration;
    private final double minConfidence;
    private final double maxPercentageMargin;
    private final StatsAssertion<PerformanceStats> forcedAssertion;
    private final boolean getSamplesUntilTimeout;
    private final int startingIterations;
    private final int startingSamples;
    private final boolean startingAutodiscoverBaseIteration;
    private final int approximateSampleMillis;

    private int[] iterations;
    private int samples;
    private boolean autodiscoverBaseIterations = true;
    private String message = null;

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public AutoProgressionPerformanceInstrumenter(
            ComposedName name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            int iterations,
            int samples,
            boolean incrementIteration,
            double minConfidence,
            double maxPercentageMargin,
            boolean autodiscoverBaseIterations,
            StatsAssertion<PerformanceStats> forcedAssertion,
            boolean getSamplesUntilTimeout,
            int approximateSampleMillis,
            PerformanceConsumer<PerformanceStats>[] performanceStatsConsumers) {
        super(name,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                performanceStatsConsumers);
        this.incrementIteration = incrementIteration;
        this.minConfidence = minConfidence;
        this.maxPercentageMargin = maxPercentageMargin;
        this.forcedAssertion = forcedAssertion;
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;

        this.startingIterations = iterations;
        this.startingSamples = samples;
        this.startingAutodiscoverBaseIteration = autodiscoverBaseIterations;
        this.approximateSampleMillis = approximateSampleMillis;

        resetProgressions();
    }

    @Override
    public AutoProgressionPerformanceInstrumenter clearTests() {
        resetProgressions();
        return super.clearTests();
    }

    private void resetProgressions() {
        this.iterations = null;
        this.samples = startingSamples;
        this.autodiscoverBaseIterations = startingAutodiscoverBaseIteration;
    }

    @Override
    protected boolean repeatExecution(final PerformanceStats stats) {
        message = null;

        // checks ANOVA and Tukey for having enough statistical convergence
        final double statsConfidence =
                stats.getStatisticalSignificanceMatrixProbability(0.9);
        if (statsConfidence < minConfidence) {
            message = "statistics not significant";
            return true;
        }

        // checks ratio percentage margin of error for maximum error allowed
        final double margin = stats.getMaximumPercentageMargin() * 100.0;
        if (margin > maxPercentageMargin) {
            message = "percentage ratio too high";
            return true;
        }

        if (forcedAssertion != null) {
            try {
                forcedAssertion.check(stats);
            } catch (AssertionError e) {
                message = "assertion: " + e.getMessage();
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    protected boolean continueTakingSamples(int sample, boolean timeout) {
        if (getSamplesUntilTimeout) {
            return !timeout;
        }
        return super.continueTakingSamples(sample, timeout);
    }

    @Override
    protected int getSamples() {
        if (getSamplesUntilTimeout) {
            return Integer.MAX_VALUE;
        }
        final int result = samples;
        if (!incrementIteration) {
            samples *= 10;
        }
        return result;
    }

    @Override
    protected int[] getIterations() {
        if (autodiscoverBaseIterations && iterations == null) {
            autodiscoverBaseIterations = false;
            iterations = getPerformanceTimer().iterationTimeEstimator(
                    approximateSampleMillis);
            return iterations;
        }
        if (iterations == null) {
            iterations = createIterationArray(startingIterations);
        }
        if (incrementIteration) {
            final int[] result = Arrays.copyOf(iterations, iterations.length);
            for (int i=0; i<iterations.length; i++) {
                iterations[i] *= 10;
            }
            return result;
        }
        return iterations;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
