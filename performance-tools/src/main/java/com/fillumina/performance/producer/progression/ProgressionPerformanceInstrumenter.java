package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.producer.timer.AbstractPerformanceTimer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.timer.IterationSettable;
import com.fillumina.performance.util.TimeUnitHelper;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The JVM continuously optimizes the code at runtime based on the running
 * statistics it collects. This process takes place in multiple steps and may
 * be triggered by different events such as configurations or the number of
 * executions of a particular piece code.
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
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionPerformanceInstrumenter
        extends AbstractInstrumentablePerformanceProducer
            <ProgressionPerformanceInstrumenter>
        implements Serializable, PerformanceExecutorInstrumenter,
            InstrumentablePerformanceExecutor<ProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final long[] iterationsProgression;
    private final long samplesPerStep;
    private final Long timeoutNanoseconds;
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
                builder.getSamplesPerStep(),
                builder.isCheckStdDeviation(),
                builder.getTimeoutInNanoseconds());
    }

    public ProgressionPerformanceInstrumenter(
            final String message,
            final long[] iterationsProgression,
            final long samplesPerStep,
            final boolean checkStandardDeviation,
            final long timeoutNanoseconds) {
        assert iterationsProgression != null && iterationsProgression.length > 0;
        assert samplesPerStep > 0;

        this.message = message;
        this.iterationsProgression = iterationsProgression;
        this.samplesPerStep = samplesPerStep;
        this.checkStdDev = checkStandardDeviation;
        this.timeoutNanoseconds = timeoutNanoseconds;
    }

    /**
     * Override if you need to stop the sequence.
     *
     * @param performances the current step's performances
     * @return {@code true} if you want to stop at this step
     */
    protected boolean stopIterating(final LoopPerformancesSequence performances) {
        return false;
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
    public ProgressionPerformanceInstrumenter warmup() {
        // the codepath for warmup must be as close as possible to execute()
        final LoopPerformances lp = executeTests();
        // this check avoids JVM cutting out dead code
        if (lp.getStatistics().min() < 0) {
            throw new AssertionError("elapsed time cannot be negative");
        }
        return this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        LoopPerformances avgLoopPerformances = executeTests();
        consume(message, avgLoopPerformances);
        return new LoopPerformancesHolder(avgLoopPerformances);
    }

    private LoopPerformances executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        prevStdDev = -1D;
        LoopPerformancesSequence.Running sequencePerformances = null;

        for (int iterationsIndex = 0;
                iterationsIndex<iterationsProgression.length;
                iterationsIndex++) {

            final long iterations = iterationsProgression[iterationsIndex];

            sequencePerformances = new LoopPerformancesSequence.Running();

            for (int sample=0; sample<samplesPerStep; sample++) {
                setIterationsIterationSettable(iterations);
                final LoopPerformances loopPerformances = performanceExecutor
                        .execute()
                        .getLoopPerformances();

                sequencePerformances.addLoopPerformances(loopPerformances);

                checkForTimeout(start);
            }

            if (stopIterating(sequencePerformances)) {
                break;
            }

            iterationsIndex = checkStandardDeviationEvolution(
                    sequencePerformances, iterationsIndex);
        }
        final LoopPerformances avgLoopPerformances =
                sequencePerformances.calculateAverageLoopPerformances();
        return avgLoopPerformances;
    }

    private void checkForTimeout(long start) {
        if (timeoutNanoseconds != null &&
                System.nanoTime() - start > timeoutNanoseconds) {
            throw new RuntimeException("Timeout occurred: test '" +
                    message +
                    "' was lasting " +
                    "more than required maximum of " +
                    TimeUnitHelper.prettyPrint(timeoutNanoseconds,
                        TimeUnit.NANOSECONDS));
        }
    }

    /**
     * {@link AbstractPerformanceTimer} needs to know how many iterations
     * it has to perform.
     */
    private void setIterationsIterationSettable(final long iterations) {
        if (performanceExecutor instanceof IterationSettable) {
            ((IterationSettable<?>)performanceExecutor)
                    .setIterations(iterations);
        }
    }

    /**
     * Decides whether to return the current {@code iterationIndex} or
     * decrease it so that is executed again. It is used in case the
     * stardard deviation is greater than the previous step meaning that
     * there has been a disturbance during the test.
     */
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
