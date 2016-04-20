package com.fillumina.performance.progression;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceSampleProducer;
import com.fillumina.performance.sample.PerformanceSampleProducerInstrumenter;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsProducerImpl;
import com.fillumina.performance.stats.PerformancesStatsHolder;
import com.fillumina.performance.util.TimeUnitHelper;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumenter
                <T extends AbstractPerformanceInstrumenter<T>>
        extends PerformanceStatsProducerImpl<T>
        implements PerformanceSampleProducerInstrumenter {

    private PerformanceSampleProducer performanceProducer;

    protected abstract int getSamples();

    protected abstract int getIterations();

    protected abstract long getTimeoutNanoseconds();

    protected abstract String getMessage();

    @Override
    @SuppressWarnings("unchecked")
    public T instrument(PerformanceSampleProducer producer) {
        this.performanceProducer = producer;
        return (T) this;
    }

    /**
     * Override if you need to stop the sequence.
     *
     * @param stats the current step's performances
     * @return {@code true} if you want to stop at this step
     */
    protected boolean stopIterating(final PerformanceStats stats) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public T warmup() {
        // the codepath for warmup must be as close as possible to execute()
        final PerformanceStats stats = executeTests();
        // this check avoids JVM cutting out dead code
        if (stats != null) {
            throw new AssertionError("elapsed time cannot be negative");
        }
        return (T) this;
    }

    public PerformancesStatsHolder execute() {
        PerformanceStats stats = executeTests();
        dispatchPerformanceToConsumers(getMessage(), stats);
        return new PerformancesStatsHolder(stats);
    }

    private PerformanceStats executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        PerformanceDataCollector collector;
        int iterations, samples;
        PerformanceSample perfSample;
        PerformanceStats stats = PerformanceStats.EMPTY;

        do {
            collector = new PerformanceDataCollector();
            samples = getSamples();
            iterations = getIterations();

            for (int sample=0; sample<samples; sample++) {
                perfSample = performanceProducer.execute(iterations);

                collector.add(perfSample);

                checkForTimeout(start);
            }

            stats = collector.createPerformanceStats();
        } while(!stopIterating(stats));

        return stats;
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

    private void assertPerformanceExecutorNotNull() {
        if (performanceProducer == null) {
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
