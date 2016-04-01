package com.fillumina.performance.producer.progression;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesSequence;
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
    private static final long serialVersionUID = 1L;

    private final String message;
    private int iterations;
    private int samples;
    private final double maxStandardDeviation;
    private final long timeoutNanoseconds;
    private final boolean incrementIteration;
    private final boolean checkStdDeviation;
    private final int garbageCollectorMillis;
    private final PerformanceConsumer loopPerformanceConsumer;

    private double oldStdDev = -1D;
    private boolean increment = true;

    private final List<StandardDeviationConsumer> standardDeviationConsumers =
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
            double maxStandardDeviation,
            long timeoutNanoseconds,
            boolean incrementIteration,
            boolean checkStdDeviation,
            int garbageCollectorMillis,
            PerformanceConsumer loopPerformanceConsumer) {
        this.message = message;
        this.iterations = iterations;
        this.samples = samples;
        this.maxStandardDeviation = maxStandardDeviation;
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.incrementIteration = incrementIteration;
        this.checkStdDeviation = checkStdDeviation;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.loopPerformanceConsumer = loopPerformanceConsumer;
    }

    @Override
    protected boolean stopIterating(final LoopPerformances averagePerformances,
            final LoopPerformancesSequence performances) {

        final double stdDev =
                performances.calculateMaximumStandardDeviation();

        callStandardDeviationConsumers(
                performances.getAverageIterations(),
                performances.getSamples(),
                stdDev);

        loopPerformanceConsumer.consume("loop", averagePerformances);

        if (garbageCollectorMillis > 0) {
            System.gc();
            try {
                Thread.sleep(garbageCollectorMillis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        increment = !checkStdDeviation || stdDev < oldStdDev;
        oldStdDev = stdDev;
        return stdDev < maxStandardDeviation;
    }

    /**
     * Adds a {@link StandardDeviationConsumer} that will be called at every
     * step with the average standard deviation of all the {@code samples}
     * of that step.
     * @param consumers the {@link StandardDeviationConsumer}
     * @return  {@code this} to allow for <i>fluent interface</i>
     */
    @SuppressWarnings("unchecked")
    public AutoProgressionPerformanceInstrumenter addStandardErrorConsumer(
            final StandardDeviationConsumer... consumers) {
        for (final StandardDeviationConsumer consumer: consumers) {
            if (consumer != null) {
                standardDeviationConsumers.add(consumer);
            }
        }
        return this;
    }

    private void callStandardDeviationConsumers(
            final long iterations, final long samples, final double stdDev) {
        for (final StandardDeviationConsumer consumer:
                standardDeviationConsumers) {
            consumer.consume(iterations, samples, stdDev);
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
}
