package com.fillumina.performance.progression;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceSampleProducer;
import com.fillumina.performance.sample.PerformanceSampleProducerInstrumenter;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStatsProducerImpl;
import com.fillumina.performance.stats.PerformancesStatsHolder;
import com.fillumina.performance.util.StringHelper;
import com.fillumina.performance.util.TimeUnitFormatter;
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
    private final String name;
    private final long timeoutNanoseconds;
    private final long garbageCollectorMillis;
    private final double confidence;
    private final boolean eliminateOutliers;

    public AbstractPerformanceInstrumenter(String name,
            long timeoutNanoseconds,
            long garbageCollectorMillis,
            double confidence,
            boolean eliminateOutliers,
            PerformanceStatsConsumer[] performanceStatsConsumers) {
        super();
        this.name = name;
        this.timeoutNanoseconds = timeoutNanoseconds;
        this.garbageCollectorMillis = garbageCollectorMillis;
        this.confidence = confidence;
        this.eliminateOutliers = eliminateOutliers;
        addPerformanceConsumer(performanceStatsConsumers);
    }

    protected abstract String getMessage();

    protected abstract int getSamples();

    protected abstract int getIterations();

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

    public PerformancesStatsHolder execute() {
        PerformanceStats stats = executeTests();
        return new PerformancesStatsHolder(stats);
    }

    private PerformanceStats executeTests() {
        assertPerformanceExecutorNotNull();

        long start = System.nanoTime();
        PerformanceDataCollector collector;
        int iterations, samples;
        PerformanceSample perfSample;
        PerformanceStats stats = PerformanceStats.EMPTY;
        boolean stopIterating;

        do {
            collector = new PerformanceDataCollector(confidence);
            samples = getSamples();
            iterations = getIterations();

            performGarbageCollection();

            for (int sample=0; sample<samples; sample++) {
                perfSample = performanceProducer.execute(iterations);

                collector.add(perfSample);

                checkForTimeout(start);
            }

            stats = collector.createPerformanceStats(eliminateOutliers);
            stopIterating = stopIterating(stats);
            dispatchPerformanceToConsumers(
                    StringHelper.concat(" ", name, getMessage()), stats);

        } while(!stopIterating);

        return stats;
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
