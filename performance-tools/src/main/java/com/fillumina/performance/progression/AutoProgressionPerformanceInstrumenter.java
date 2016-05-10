package com.fillumina.performance.progression;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
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
    private int iterations;
    private int samples;
    private String message = null;
    private boolean autodiscoverBaseIterations = true;
    private PerformanceAssertion forcedAssertion;

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
            boolean addBaselineTest,
            PerformanceAssertion forcedAssertion,
            PerformanceStatsConsumer[] performanceStatsConsumers) {
        super(message,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                addBaselineTest,
                performanceStatsConsumers);
        this.iterations = iterations;
        this.samples = samples;
        this.incrementIteration = incrementIteration;
        this.minConfidence = minConfidence;
        this.maxPercentageMargin = maxPercentageMargin;
        this.autodiscoverBaseIterations = autodiscoverBaseIterations;
        this.forcedAssertion = forcedAssertion;
    }

    @Override
    protected boolean stopIterating(final PerformanceStats stats) {
        message = "";

        // checks ANOVA and Tukey for having enough statistical convergence
        final double statsConfidence =
                stats.getStatisticalSignificanceMatrixProbability();
        if (statsConfidence < minConfidence) {
            message = "statistics not significant";
            return false;
        }

        // checks ratio percentage margin of error for maximum error allowed
        final double margin = stats.getMaximumPercentageMargin();
        if (margin > maxPercentageMargin) {
            message = "percentage ratio too big";
            return false;
        }

        if (forcedAssertion != null) {
            try {
                forcedAssertion.check(stats);
            } catch (AssertionError e) {
                message = e.getMessage();
                return false;
            }
            return true;
        }

        return true;
    }

    @Override
    protected int getSamples() {
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
            iterations = getPerformanceProducer().iterationTimeEstimator(250);
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
