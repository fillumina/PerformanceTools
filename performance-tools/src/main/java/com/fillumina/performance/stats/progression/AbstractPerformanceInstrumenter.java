package com.fillumina.performance.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.StatsProducer;
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

    private final AbstractTestable BASELINE_TEST = new AbstractTestable() {
        private int counter = 0;
        @Override
        public Object test() {
            return counter++;
        }
    };

    private PerformanceTimer performanceTimer;
    private final String name;
    private final long timeoutNanoseconds;
    private final long garbageCollectorMillis;
    private final double confidence;
    private final boolean eliminateOutliers;
    private boolean addBaselineTest;

    public AbstractPerformanceInstrumenter(String name,
            long timeoutNanoseconds,
            long garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            boolean addBaselineTest,
            PerformanceConsumer<PerformanceStats>[] performanceStatsConsumers) {
        super();
        this.name = name;
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.confidence = confidence;
        this.eliminateOutliers = eliminateOutliers;
        this.addBaselineTest = addBaselineTest;
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
    protected boolean stopIterating(final PerformanceStats stats) {
        return false;
    }

    @Override
    public PerformanceHolder<PerformanceStats> execute() {
        performanceTimer.reset();
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            performanceTimer.addTest(entry.getKey(), entry.getValue());
        }

        PerformanceStats stats = executeTests();
        performanceTimer.reset();
        return new PerformanceHolder<>(stats);
    }

    private PerformanceStats executeTests() {
        assertPerformanceExecutorNotNull();
        addNullTest();

        long start = System.nanoTime();
        PerformanceDataCollector collector;
        int iterations, samples;
        PerformanceSample perfSample;
        PerformanceStats stats = null;
        boolean stopIterating;

        do {
            collector = new PerformanceDataCollector(confidence);
            samples = getSamples();
            iterations = getIterations();

            performGarbageCollection();

            for (int sample=0; sample<samples; sample++) {
                perfSample = performanceTimer.execute(iterations);

                collector.add(perfSample);

                checkForTimeout(start);
            }

            stats = collector.createPerformanceStats(getMessage(),
                    eliminateOutliers);
            stopIterating = stopIterating(stats);
            dispatchToConsumers(name, stats);

        } while(!stopIterating);

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

    private void checkForTimeout(long start) {
        if (timeoutNanoseconds > 0 &&
                System.nanoTime() - start > timeoutNanoseconds) {
            String testName = (name == null || name.isEmpty()) ? "" :
                    "'" + name + "' ";
            throw new RuntimeException("Timeout occurred: test " + testName +
                    "was lasting " +
                    "more than required maximum of " +
                    TimeUnitFormatter.prettyPrint(timeoutNanoseconds,
                        TimeUnit.NANOSECONDS));
        }
    }

    private void addNullTest() {
        if (addBaselineTest) {
            performanceTimer.addTest(
                    PerformanceStats.BASELINE_TEST_NAME, BASELINE_TEST);
            addBaselineTest = false;
        }
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
    public I reset() {
        performanceTimer.reset();
        return super.reset();
    }

    @Override
    public <T extends Instrumenter<StatsProducer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
