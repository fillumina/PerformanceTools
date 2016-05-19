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
import com.fillumina.performance.stats.baseline.BaselineHelper;
import com.fillumina.performance.stats.baseline.TestableBaseline;
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
    private final String name;
    private final long timeoutNanoseconds;
    private final long garbageCollectorMillis;
    private final double confidence;
    private final boolean eliminateOutliers;
    private final TestableBaseline baseline;

    public AbstractPerformanceInstrumenter(String name,
            long timeoutNanoseconds,
            long garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            TestableBaseline baseline,
            PerformanceConsumer<PerformanceStats>[] performanceStatsConsumers) {
        super();
        this.name = name;
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.confidence = confidence;
        this.eliminateOutliers = eliminateOutliers;
        this.baseline = baseline;
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
    protected boolean repeatExecution(final PerformanceStats stats) {
        return true;
    }

    @Override
    public PerformanceHolder<PerformanceStats> execute() {
        addDefaultBaselineTest();
        assertPerformanceExecutorNotNull();
        performanceTimer.resetTests();
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            performanceTimer.addTest(entry.getKey(), entry.getValue());
        }

        PerformanceStats stats = executeTests();
        performanceTimer.resetTests();
        return new PerformanceHolder<>(stats);
    }

    private void addDefaultBaselineTest() {
        if (baseline != null &&
                !BaselineHelper.INSTANCE.isBaselinePresent(getTests())) {
            final String testName =
                    BaselineHelper.INSTANCE.createBaselineName(baseline);
            addTest(testName, baseline);
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

            performGarbageCollection();

            int sample = 0;
            while (continueTakingSamples(sample, isTimeout(start))) {
                perfSample = performanceTimer.execute(iterations);
                collector.add(perfSample);
                sample++;
            }

            stats = collector.createPerformanceStats(getMessage(),
                    eliminateOutliers);
            repeatExecution = repeatExecution(stats);
            dispatchToConsumers(name, stats);

        } while(repeatExecution);

        return PerformanceStats.copyWithNewMessage(stats, null);
    }

    private void performGarbageCollection() {
        if (garbageCollectorMillis >= 0) {
            System.gc();
            try {
                Thread.sleep(garbageCollectorMillis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected void throwTimeoutException() {
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
