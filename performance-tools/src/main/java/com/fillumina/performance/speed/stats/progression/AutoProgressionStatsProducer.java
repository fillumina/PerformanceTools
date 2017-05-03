package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

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
public class AutoProgressionStatsProducer
        extends AbstractProgressionStatsProducer
            <AutoProgressionStatsProducer> {

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

    public static AutoProgressionStatsProducerBuilder builder() {
        return new AutoProgressionStatsProducerBuilder();
    }

    public AutoProgressionStatsProducer(
            TName name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            boolean filterSamples,
            boolean coolDownCpu,
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
                filterSamples,
                coolDownCpu,
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
    public AutoProgressionStatsProducer clearTests() {
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
        final double margin = stats.getMaximumPercentageMargin(Ratio.P_95)
                .getPercentage();
        if (margin > maxPercentageMargin) {
            message = String.format(Locale.US,
                    "percentage ratio %.2f %% too high, " +
                    "required less than %.2f %%", margin, maxPercentageMargin);
            return true;
        }

        if (forcedAssertion != null) {
            try {
                forcedAssertion.check(getName(), stats);
            } catch (AssertionError e) {
                StringBuilder buf = new StringBuilder();
                try {
                    forcedAssertion.toString(buf, stats);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                message = "failed assertion: " + buf.toString();

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
            final PerformanceTimer pt = getPerformanceTimer();
            iterations = pt.iterationTimeEstimatorMs(approximateSampleMillis);
            minIteration = calculateMinIteration(iterations);
            return iterations;
        }
        if (iterations == null) {
            minIteration = startingIterations;
            iterations = createIterationsArray(startingIterations);
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
