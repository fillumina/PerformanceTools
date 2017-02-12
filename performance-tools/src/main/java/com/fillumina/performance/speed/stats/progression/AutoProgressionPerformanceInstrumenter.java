package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.ComposedName;
import java.util.Arrays;

/**
 * Automatically finds the optimal parameters to perform a performance
 * estimation of the code under test. It increases the number of iterations
 * or samples (according to configuration) to meet the accuracy requirements.
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
    private final double maxPercentageMargin;
    private final StatsAssertion<?,SpeedStats> forcedAssertion;
    private final boolean getSamplesUntilTimeout;
    private final int startingIterations;
    private final int startingSamples;
    private final boolean startingAutodiscoverBaseIteration;
    private final int approximateSampleMillis;

    private int[] iterations;
    private int minIteration;
    private int samples = -1;
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
            double maxPercentageMargin,
            boolean autodiscoverBaseIterations,
            StatsAssertion<?,SpeedStats> forcedAssertion,
            boolean getSamplesUntilTimeout,
            int approximateSampleMillis,
            PerformanceConsumer<SpeedStats>[] performanceStatsConsumers) {
        super(name,
                timeoutNanoseconds,
                garbageCollectorMillis,
                confidence,
                eliminateOutliers,
                performanceStatsConsumers);
        this.incrementIteration = incrementIteration;
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
    protected boolean repeatExecution(final SpeedStats stats) {
        message = null;

        // checks ratio percentage margin of error for maximum error allowed
        final double margin = stats.getMaximumPercentageMargin() * 100.0;
        if (margin > maxPercentageMargin) {
            message = String.format("percentage ratio %.2f %% too high, " +
                    "required less than %.2f %%", margin, maxPercentageMargin);
//            System.out.println(message);
            return true;
        }

        if (forcedAssertion != null) {
            PerformanceHolder<SpeedStats> holder =
                    new PerformanceHolder<>(getName(), stats);
            try {
                forcedAssertion.check(holder);
            } catch (AssertionError e) {
                message = "failed assertion: " + forcedAssertion.toString(holder);
//                System.out.println(message);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean continueTakingSamples(SampleProgressionStatus status,
            boolean timeout) {
        if (getSamplesUntilTimeout) {
            return !timeout;
        }
        return super.continueTakingSamples(status, timeout);
    }

    @Override
    protected int getSamples() {
        if (getSamplesUntilTimeout) {
            return Integer.MAX_VALUE;
        }
        if (samples == -1) {
            samples = Math.max(33, 2000 / minIteration);
            return samples;
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
            minIteration = calculateMinIteration(iterations);
            return iterations;
        }
        if (iterations == null) {
            minIteration = startingIterations;
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
    public String getRejectionMessage() {
        return message;
    }

    private static int calculateMinIteration(int[] iterations) {
        int min = Integer.MAX_VALUE;
        for (int i : iterations) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

}
