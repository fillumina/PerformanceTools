package com.fillumina.performance.progression;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.TestPerformances;
import java.util.ArrayList;
import java.util.List;

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
public class AutoProgressionPerformanceInstrumenter
        extends AbstractPerformanceInstrumenter
            <AutoProgressionPerformanceInstrumenter> {

    private final String message;
    private int iterations;
    private int samples;
    private final long timeoutNanoseconds;
    private final boolean incrementIteration;
    private final double minConfidence;
    private final double maxPercentageMargin;
    private final double confidence;
    private final long garbageCollectorMills;
    private final boolean eliminateOutliers;

    private boolean increment = true;

    private final List<ConfidenceConsumer> confidenceConsumers =
            new ArrayList<>();

    public static AutoProgressionPerformanceInstrumenterBuilder builder() {
        return new AutoProgressionPerformanceInstrumenterBuilder();
    }

    public static AutoProgressionPerformanceInstrumenter create() {
        return new AutoProgressionPerformanceInstrumenterBuilder().build();
    }

    public AutoProgressionPerformanceInstrumenter(
            String message,
            int iterations,
            int samples,
            double minConfidence,
            long timeoutNanoseconds,
            boolean incrementIteration,
            double confidence,
            double maxPercentageMargin,
            long garbageCollectorMills,
            PerformanceStatsConsumer performanceStatsConsumer,
            boolean eliminateOutliers) {
        super();
        this.message = message;
        this.iterations = iterations;
        this.samples = samples;
        this.minConfidence = minConfidence;
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.incrementIteration = incrementIteration;
        this.confidence = confidence;
        this.garbageCollectorMills = garbageCollectorMills;
        this.maxPercentageMargin = maxPercentageMargin;
        this.eliminateOutliers = eliminateOutliers;
        addPerformanceConsumer(performanceStatsConsumer);
    }

    @Override
    protected boolean stopIterating(final PerformanceStats stats) {
        final double statsConfidence = stats.getConfidence();
        final TestPerformances test = stats.getTestPerformances()
                .values().iterator().next();
        long it = test.getIterations();
        long s = test.getElapsedNanosecondsPerCycle().count();
        callConfidenceConsumers(it, s, statsConfidence);

        if (statsConfidence < minConfidence) {
            return false;
        }
        final double margin = stats.getMaximumPercentageMargin();
        return margin <= maxPercentageMargin;
    }

    @SuppressWarnings("unchecked")
    public AutoProgressionPerformanceInstrumenter addConfidenceConsumerIf(
            final boolean condition,
            final ConfidenceConsumer... consumers) {
        if (condition) {
            addConfidenceConsumer(consumers);
        }
        return this;
    }

    /**
     * Adds a {@link ConfidenceConsumer} that will be called at every
     * step with the average standard deviation of all the {@code samples}
     * of that step.
     * @param consumers the {@link ConfidenceConsumer}
     * @return  {@code this} to allow for <i>fluent interface</i>
     */
    @SuppressWarnings("unchecked")
    public AutoProgressionPerformanceInstrumenter addConfidenceConsumer(
            final ConfidenceConsumer... consumers) {
        for (final ConfidenceConsumer consumer: consumers) {
            if (consumer != null) {
                confidenceConsumers.add(consumer);
            }
        }
        return this;
    }

    @Override
    protected long getGarbageCollectorMillis() {
        return garbageCollectorMills;
    }

    private void callConfidenceConsumers(
            final long iterations, final long samples, final double confidence) {
        for (final ConfidenceConsumer consumer:
                confidenceConsumers) {
            consumer.consume(iterations, samples, confidence);
        }
    }

    @Override
    protected int getSamples() {
        final int result = samples;
        if (!incrementIteration && increment) {
            samples *= 10;
        }
        return result;
    }

    @Override
    protected int getIterations() {
        final int result = iterations;
        if (incrementIteration && increment) {
            iterations *= 10;
        }
        return result;
    }

    @Override
    protected long getTimeoutNanoseconds() {
        return timeoutNanoseconds;
    }

    @Override
    protected String getMessage() {
        return message;
    }

    @Override
    protected double getConfidence() {
        return confidence;
    }

    @Override
    protected boolean isEliminatingOutliers() {
        return eliminateOutliers;
    }
}
