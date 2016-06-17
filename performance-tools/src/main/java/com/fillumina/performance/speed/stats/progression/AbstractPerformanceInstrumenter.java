package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.PerformanceSampleCollector;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.speed.stats.StatsProducer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.TimeUnitFormatter;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Map;

/**
 * Base class for other progression performance instrumenters.
 *
 * @see ProgressionPerformanceInstrumenter
 * @see AutoProgressionPerformanceInstrumenter
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumenter
                <I extends AbstractPerformanceInstrumenter<I>>
        extends AbstractPerformanceProducer<I, PerformanceStats, Testable>
        implements Instrumenter<PerformanceTimer>,
                   StatsProducer<PerformanceStats> {

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

    /** @return an error message. */
    protected abstract String getMessage();

    /** @return the number of samples to take. */
    protected abstract int getSamples();

    /** @return the number of iterations for each sample. */
    protected abstract int[] getIterations();

    protected boolean continueTakingSamples(int sample, boolean timeout) {
        if (timeout) {
            throwTimeoutException();
        }
        return true;
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
        performanceTimer.clearTests();
        return new PerformanceHolder<>(stats);
    }

    protected void addTestsToPerformanceTimer() {
        performanceTimer.clearTests();
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            performanceTimer.addTest(entry.getKey(), entry.getValue());
        }
    }

    private PerformanceStats executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        PerformanceSampleCollector collector;
        int[] iterations;
        int samples;
        PerformanceSample perfSample;
        PerformanceStats stats = null;

        do {
            collector = new PerformanceSampleCollector(confidence);
            iterations = getIterations();
            samples = getSamples();

            performGarbageCollection(garbageCollectorMillis);

            int sample = 0;
            do {
                perfSample = performanceTimer.execute(iterations);
                collector.add(perfSample);
                sample++;
            } while (sample < samples &&
                    continueTakingSamples(sample, isTimeout(start)));

            stats = collector.createPerformanceStats(eliminateOutliers);
            dispatchToConsumers(getName().add(getMessage()), stats);

        } while(repeatExecution(stats));

        return stats;
    }

    protected void throwTimeoutException() {
        String name = getName().toString();
        String testName = (name == null || name.isEmpty()) ? "" :
                "'" + name + "' ";
        throw new RuntimeException("Timeout occurred: test " + testName +
                "was lasting " +
                "more than required maximum of " +
                TimeUnitFormatter.prettyPrint(timeoutNanoseconds));
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
    public <T extends Instrumenter<StatsProducer<PerformanceStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
