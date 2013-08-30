package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.AbstractPerformanceTimer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.util.TimeUnitHelper;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The JVM actively optimizes the code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and may
 * be triggered by different events such as configurations or the number of
 * executions of a particular piece code.
 * That means that if you measure the performance on a small amount of iterations
 * you may not capture the performances of the full optimized code. To better
 * understand the point from which the performances stabilize this class
 * run several tests incrementing the iterations number in successive steps.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractInstrumentablePerformanceProducer<ProgressionPerformanceInstrumenter>
        implements Serializable, PerformanceExecutorInstrumenter,
            InstrumentablePerformanceExecutor<ProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final long[] iterationsProgression;
    private final long samplesPerMagnitude;
    private final Long timeout;
    private final String message;
    private final boolean checkStdDev;

    private InstrumentablePerformanceExecutor<?> performanceExecutor;
    private double prevStdDev = -1D;

    public static ProgressionPerformanceInstrumenterBuilder builder() {
        return new ProgressionPerformanceInstrumenterBuilder();
    }

    public ProgressionPerformanceInstrumenter(
            final ProgressionPerformanceInstrumenterBuilder builder) {
        this(builder.getMessage(),
                builder.getIterationsProgression(),
                builder.getSamplesPerMagnitude(),
                builder.isCheckStdDeviation(),
                builder.getTimeoutInNanoseconds());
    }

    public ProgressionPerformanceInstrumenter(
            final String message,
            final long[] iterationsProgression,
            final long samplesPerMagnitude,
            final boolean checkStandardDeviation,
            final long timeout) {
        assert iterationsProgression != null && iterationsProgression.length > 0;
        assert samplesPerMagnitude > 0;

        this.message = message;
        this.iterationsProgression = iterationsProgression;
        this.samplesPerMagnitude = samplesPerMagnitude;
        this.checkStdDev = checkStandardDeviation;
        this.timeout = timeout;
    }

    /** Override if you need to stop the sequence. */
    protected boolean stopIterating(final LoopPerformancesSequence serie) {
        return false;
    }

    @Override
    public <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public PerformanceExecutorInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        this.performanceExecutor = performanceExecutor;
        return this;
    }

    @Override
    public ProgressionPerformanceInstrumenter addTest(
            final String name,
            final Runnable test) {
        performanceExecutor.addTest(name, test);
        return this;
    }

    @Override
    public ProgressionPerformanceInstrumenter ignoreTest(
            final String name,
            final Runnable test) {
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        return execute(true);
    }

    @Override
    public ProgressionPerformanceInstrumenter warmup() {
        execute(false);
        return this;
    }

    private LoopPerformancesHolder execute(final boolean reportStatistics) {
        assertPerformanceExecutorNotNull();
        long start = System.nanoTime();
        LoopPerformancesSequence sequencePerformances = null;

        for (int iterationsIndex = 0;
                iterationsIndex<iterationsProgression.length;
                iterationsIndex++) {
            final long iterations = iterationsProgression[iterationsIndex];

            if (reportStatistics) {
                sequencePerformances = new LoopPerformancesSequence();
            }

            for (int sample=0; sample<samplesPerMagnitude; sample++) {
                setIterationsIfNeeded(iterations);
                final LoopPerformances loopPerformances = performanceExecutor
                        .execute()
                        .getLoopPerformances();

                if (reportStatistics) {
                    sequencePerformances.addLoopPerformances(loopPerformances);
                }

                checkForTimeout(start);
            }

            if (reportStatistics) {
                if (stopIterating(sequencePerformances)) {
                    break;
                }

                iterationsIndex = checkStandardDeviationEvolution(
                        sequencePerformances, iterationsIndex);
            }
        }

        if (reportStatistics) {
            final LoopPerformances avgLoopPerformances =
                    sequencePerformances.calculateAverageLoopPerformances();

            consume(message, avgLoopPerformances);

            return new LoopPerformancesHolder(avgLoopPerformances);
        }
        return null;
    }

    private void checkForTimeout(long start) {
        if (timeout != null && System.nanoTime() - start > timeout) {
            throw new RuntimeException("Timeout occurred: test '" +
                    message +
                    "' was lasting " +
                    "more than required maximum of " +
                    TimeUnitHelper.prettyPrint(timeout, TimeUnit.NANOSECONDS));
        }
    }

    public void setIterationsIfNeeded(final long iterations) {
        if (performanceExecutor instanceof AbstractPerformanceTimer) {
            ((AbstractPerformanceTimer<?>)performanceExecutor)
                    .setIterations(iterations);
        }
    }

    private int checkStandardDeviationEvolution(
            final LoopPerformancesSequence sequencePerformances,
            int iterationsIndex) {
        if (checkStdDev) {
            final double stdDev =
                    sequencePerformances.calculateMaximumStandardDeviation();
            if (prevStdDev != -1 && stdDev > prevStdDev) {
                iterationsIndex--;
            }
            prevStdDev = stdDev;
        }
        return iterationsIndex;
    }

    private void assertPerformanceExecutorNotNull() {
        if (performanceExecutor == null) {
            throw new IllegalStateException(getClass().getCanonicalName() +
                ": an instrumentable class must be provided with instrument()");
        }
    }
}
