package com.fillumina.performance.producer.progression;

import com.fillumina.performance.executor.IterationSettable;
import com.fillumina.performance.executor.Testable;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.LoopPerformancesSequence;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import com.fillumina.performance.util.TimeUnitHelper;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumenter
                <T extends AbstractPerformanceInstrumenter<T>>
        extends AbstractInstrumentablePerformanceProducer<T>
        implements Serializable, PerformanceExecutorInstrumenter,
            InstrumentablePerformanceExecutor<T> {
    private static final long serialVersionUID = 1L;

    private InstrumentablePerformanceExecutor<?> performanceExecutor;

    protected abstract int getSamples();

    protected abstract int getIterations();

    protected abstract long getTimeoutNanoseconds();

    protected abstract String getMessage();

    /**
     * Override if you need to stop the sequence.
     *
     * @param performances the current step's performances
     * @return {@code true} if you want to stop at this step
     */
    protected boolean stopIterating(final LoopPerformances avgLoopPerformances,
            final LoopPerformancesSequence performances) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        this.performanceExecutor = performanceExecutor;
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T addTest(
            final String name,
            final Testable test) {
        performanceExecutor.addTest(name, test);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T ignoreTest(
            final String name,
            final Testable test) {
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T warmup() {
        // the codepath for warmup must be as close as possible to execute()
        final LoopPerformances lp = executeTests();
        // this check avoids JVM cutting out dead code
        if (lp.getStatistics().min() < 0) {
            throw new AssertionError("elapsed time cannot be negative");
        }
        return (T) this;
    }

    @Override
    public LoopPerformancesHolder execute() {
        LoopPerformances avgLoopPerformances = executeTests();
        dispatchPerformanceToConsumers(getMessage(), avgLoopPerformances);
        return new LoopPerformancesHolder(avgLoopPerformances);
    }

    // TODO keep track of the best statistics
    // TODO find when the statistics are worsening and stop the test (count from best stats)
    // TODO we can repeat the successful test and mean it to get the best result
    private LoopPerformances executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        LoopPerformancesSequence.Running sequencePerformances = null;
        int iterations, samples;
        LoopPerformances loopPerformances;
        LoopPerformances avgLoopPerformances = LoopPerformances.EMPTY;

        do {
            sequencePerformances = new LoopPerformancesSequence.Running();
            samples = getSamples();
            iterations = getIterations();

            for (int sample=0; sample<samples; sample++) {
                setIterations(iterations);
                loopPerformances = performanceExecutor
                        .execute()
                        .getLoopPerformances();

                sequencePerformances.addLoopPerformances(loopPerformances);

                checkForTimeout(start);
            }

            avgLoopPerformances =
                    sequencePerformances.calculateAverageLoopPerformances();
        } while(!stopIterating(avgLoopPerformances, sequencePerformances));

        return avgLoopPerformances;
    }

    private void checkForTimeout(long start) {
        long timeoutNanoseconds = getTimeoutNanoseconds();
        if (timeoutNanoseconds > 0 &&
                System.nanoTime() - start > timeoutNanoseconds) {
            throw new RuntimeException("Timeout occurred: test '" +
                    getMessage() +
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
    private void setIterations(final int iterations) {
        if (performanceExecutor instanceof IterationSettable) {
            ((IterationSettable<?>)performanceExecutor)
                    .setIterations(iterations);
        }
    }

    private void assertPerformanceExecutorNotNull() {
        if (performanceExecutor == null) {
            throw new IllegalStateException(getClass().getCanonicalName() +
                ": an instrumentable class must be provided with instrument()");
        }
    }

    /** Assert positive non zero. */
    protected void assertStrictlyPositive(final int positiveValue,
            final String name) {
        if (positiveValue <= 0) {
            throw new IllegalArgumentException(name +
                    " cannot be negative or zero: " +
                    positiveValue);
        }
    }
}
