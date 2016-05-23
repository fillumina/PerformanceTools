package com.fillumina.performance.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;

/**
 * Instrumenter that increases the number of iterations until a target
 * stability is met.
 * <p>
 * It produces statistics based on the average results of the last round of
 * iterations.
 * Beware that the execution can be very long so set a sensible timeout and,
 * if it takes too long, try to relax the maximum allowed standard deviation.
 *
 * @author Francesco Illuminati
 */
//TODO print parameters
public class AutoProgressionPerformanceInstrumenter
        extends AbstractPerformanceInstrumenter
            <AutoProgressionPerformanceInstrumenter> {

    private final boolean incrementIteration;
    private final double minConfidence;
    private final double maxPercentageMargin;
    private final PerformanceAssertion forcedAssertion;
    private final boolean getSamplesUntilTimeout;

    private int iterations;
    private int samples;
    private String message = null;
    private boolean autodiscoverBaseIterations = true;

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public static AutoProgressionPerformanceInstrumenter create() {
        return new AutoProgressionPerformanceInstrumenterBuilder().build();
    }

    public AutoProgressionPerformanceInstrumenter(
            String message,
            long timeoutNanoseconds,
            long garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            int iterations,
            int samples,
            boolean incrementIteration,
            double minConfidence,
            double maxPercentageMargin,
            boolean autodiscoverBaseIterations,
            PerformanceAssertion forcedAssertion,
            boolean getSamplesUntilTimeout,
            PerformanceConsumer[] performanceStatsConsumers) {
        super(message,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                performanceStatsConsumers);
        this.iterations = iterations;
        this.samples = samples;
        this.incrementIteration = incrementIteration;
        this.minConfidence = minConfidence;
        this.maxPercentageMargin = maxPercentageMargin;
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        this.forcedAssertion = forcedAssertion;
        this.getSamplesUntilTimeout = getSamplesUntilTimeout;
    }

    @Override
    protected boolean repeatExecution(final PerformanceStats stats) {
        message = "";

        // checks ANOVA and Tukey for having enough statistical convergence
        final double statsConfidence =
                stats.getStatisticalSignificanceMatrixProbability();
        if (statsConfidence < minConfidence) {
            message = "statistics not significant";
            return true;
        }

        // checks ratio percentage margin of error for maximum error allowed
        final double margin = stats.getMaximumPercentageMargin();
        if (margin > maxPercentageMargin) {
            message = "percentage ratio too big";
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
        if (sample > 10 && getSamplesUntilTimeout) {
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
    protected int getIterations() {
        if (autodiscoverBaseIterations) {
            autodiscoverBaseIterations = false;
            iterations = getPerformanceTimer().iterationTimeEstimator(250);
            return iterations;
        }
        final int result = iterations;
        if (incrementIteration) {
            iterations *= 10;
        }
        return result;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
