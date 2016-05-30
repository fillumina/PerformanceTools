package com.fillumina.performance.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.StatsProducer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TimeUnitFormatter;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumenter
                <I extends AbstractPerformanceInstrumenter<I>>
        extends AbstractPerformanceProducer<I, PerformanceStats, Testable>
        implements Instrumenter<PerformanceTimer>, StatsProducer {

    private PerformanceTimer performanceTimer;
    private final long timeoutNanoseconds;
    private final int garbageCollectorMillis;
    private final double confidence;
    private final boolean eliminateOutliers;

    public AbstractPerformanceInstrumenter(ComposedName name,
            long timeoutNanoseconds,
            int garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            PerformanceConsumer<PerformanceStats>[] performanceStatsConsumers) {
        super();
        setName(name);
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.confidence = confidence;
        this.eliminateOutliers = eliminateOutliers;
        if (performanceStatsConsumers != null) {
            for (PerformanceConsumer<PerformanceStats> pc :
                    performanceStatsConsumers) {
                addPerformanceConsumer(pc);
            }
        }
    }

    protected abstract String getMessage();

    protected abstract int getSamples();

    protected abstract int getIterations();

    protected boolean continueTakingSamples(int sample, boolean timeout) {
        if (timeout) {
            throwTimeoutException();
        }
        return sample < getSamples();
    }

    protected PerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    /** Accepts only {@link PerformanceTimer} producers. */
    @Override
    @SuppressWarnings("unchecked")
    public I instrument(PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return (I) this;
    }

    /**
     * Override if you need to stop the sequence.
     *
     * @param stats the current step's performances
     * @return {@code true} if you want to stop at this step
     */
    protected abstract boolean repeatExecution(final PerformanceStats stats);

    @Override
    public PerformanceHolder<PerformanceStats> execute() {
        assertPerformanceExecutorNotNull();
        addTestsToPerformanceTimer();
        performanceTimer.setName(getName());
        PerformanceStats stats = executeTests();
        performanceTimer.resetTests();
        return new PerformanceHolder<>(stats);
    }

    protected void addTestsToPerformanceTimer() {
        performanceTimer.resetTests();
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            performanceTimer.addTest(entry.getKey(), entry.getValue());
        }
    }

    private PerformanceStats executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        PerformanceDataCollector collector;
        int iterations;
        PerformanceSample perfSample;
        PerformanceStats stats = null;
        boolean repeatExecution;

        do {
            collector = new PerformanceDataCollector(confidence);
            iterations = getIterations();

            performGarbageCollection(garbageCollectorMillis);

            int sample = 0;
            do {
                perfSample = performanceTimer.execute(iterations);
                collector.add(perfSample);
                sample++;
            } while (continueTakingSamples(sample, isTimeout(start)));

            stats = collector.createPerformanceStats(getMessage(),
                    eliminateOutliers);

            repeatExecution = repeatExecution(stats);

            dispatchToConsumers(getName(), stats);

        } while(repeatExecution);

        return PerformanceStats.copyWithNewMessage(stats, null);
    }

    protected void throwTimeoutException() {
        String name = getName().toString();
        String testName = (name == null || name.isEmpty()) ? "" :
                "'" + name + "' ";
        throw new RuntimeException("Timeout occurred: test " + testName +
                "was lasting " +
                "more than required maximum of " +
                TimeUnitFormatter.prettyPrint(timeoutNanoseconds,
                    TimeUnit.NANOSECONDS));
    }

    private boolean isTimeout(long start) {
        return timeoutNanoseconds > 0 &&
                System.nanoTime() - start > timeoutNanoseconds;
    }

    private void assertPerformanceExecutorNotNull() {
        if (performanceTimer == null) {
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

    @Override
    public <T extends Instrumenter<StatsProducer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
