package com.fillumina.performance.producer.progression;

import com.fillumina.performance.executor.AbstractPerformanceTimer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public class ConstantIterationsPerformanceInstrumenter
        extends AbstractInstrumentablePerformanceProducer
            <ConstantIterationsPerformanceInstrumenter>
        implements Serializable, PerformanceExecutorInstrumenter,
            InstrumentablePerformanceExecutor<ConstantIterationsPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    private final int iterations;
    private final int samples;
    private final long timeoutNanoseconds;
    private final String message;
    private final double targetStandardDeviation;
    private final List<StandardDeviationConsumer> standardDeviationConsumers =
            new ArrayList<>();

    private InstrumentablePerformanceExecutor<?> performanceExecutor;

    public static ProgressionPerformanceInstrumenterBuilder builder() {
        return new ProgressionPerformanceInstrumenterBuilder();
    }

    public ConstantIterationsPerformanceInstrumenter(
            final String message,
            final int iterations,
            final int samplesPerStep,
            final double targetStandardDeviation,
            final long timeoutSeconds) {
        assert iterations > 0;
        assert samplesPerStep > 0;

        this.message = message;
        this.iterations = iterations;
        this.samples = samplesPerStep;
        this.targetStandardDeviation = targetStandardDeviation;
        this.timeoutNanoseconds = timeoutSeconds * 1_000_000_000L;
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
    public ConstantIterationsPerformanceInstrumenter addTest(
            final String name,
            final Testable test) {
        performanceExecutor.addTest(name, test);
        return this;
    }

    @Override
    public ConstantIterationsPerformanceInstrumenter ignoreTest(
            final String name,
            final Testable test) {
        return this;
    }

    @Override
    public ConstantIterationsPerformanceInstrumenter warmup() {
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
        dispatchPerformanceToConsumers(message, avgLoopPerformances);
        return new LoopPerformancesHolder(avgLoopPerformances);
    }

    private LoopPerformances executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        LoopPerformancesSequence.Running sequencePerformances =
            new LoopPerformancesSequence.Running();

        while (true) {

            for (int sample=0; sample<samples; sample++) {
                setIterations(iterations);
                final LoopPerformances loopPerformances = performanceExecutor
                        .execute()
                        .getLoopPerformances();

                sequencePerformances.addLoopPerformances(loopPerformances);
                dispatchPerformanceToConsumers(message, loopPerformances);
            }

            callStandardDeviationConsumers(
                    sequencePerformances.getAverageIterations(),
                    sequencePerformances.getSamples(),
                    sequencePerformances.calculateMaximumStandardDeviation());

            checkForTimeout(start);

            if (stopIterating(sequencePerformances)) {
                break;
            }

            if (sequencePerformances.calculateMaximumStandardDeviation() <=
                    targetStandardDeviation) {
                break;
            }
        }
        final LoopPerformances avgLoopPerformances =
                sequencePerformances.calculateAverageLoopPerformances();
        return avgLoopPerformances;
    }

    private void checkForTimeout(long start) {
        if (System.nanoTime() - start > timeoutNanoseconds ) {
            throw new RuntimeException("Timeout occurred: test '" +
                    message +
                    "' was lasting " +
                    "more than required maximum of " +
                    TimeUnitHelper.prettyPrint(timeoutNanoseconds,
                        TimeUnit.SECONDS));
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

    /**
     * Adds a {@link StandardDeviationConsumer} that will be called at every
     * step with the average standard deviation of all the {@code samples}
     * of that step.
     * @param consumers the {@link StandardDeviationConsumer}
     * @return  {@code this} to allow for <i>fluent interface</i>
     */
    public ConstantIterationsPerformanceInstrumenter addStandardDeviationConsumer(
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

}
