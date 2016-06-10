package com.fillumina.performance.stats;

import com.fillumina.performance.infrastructure.AbstractPerformanceConsumerNotifier;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.IterationTimeCollector;

/**
 * Extracts performances out of an existing code with a stopwatch timer
 * paradigm.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimer
        extends AbstractPerformanceConsumerNotifier
                    <StopWatchTimer,PerformanceStats>{

    private final PerformanceSampleCollector sampleCollector;
    private IterationTimeCollector timeCollector;
    private long last;

    public StopWatchTimer() {
        this(new PerformanceSampleCollector());
    }

    public StopWatchTimer(PerformanceSampleCollector sampleCollector) {
        this.sampleCollector = sampleCollector;
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
        if (timeCollector != null) {
            sampleCollector.add(timeCollector.createPerformanceSample());
        }
        timeCollector = new IterationTimeCollector();
        last = System.nanoTime();
        return true;
    }

    /**
     * Accounts the time elapsed since the call to {@link #start()} or the
     * last call to {@link #section(String)} to named section.
     */
    public boolean section(final String name) {
        return section(name, 1);
    }

    /**
     * Accounts the time elapsed since the call to {@link #start()} or the
     * last call to {@link #section(String)} to named section specifying
     * how many iterations the code has completed.
     */
    public boolean section(final String name, final int iteration) {
        final long segment = System.nanoTime() - last;
        timeCollector.add(name, segment, iteration);
        last = System.nanoTime();
        return true;
    }

    /** Stop the timer. It must be called at the end of each iteration. */
    public boolean stop() {
        if (timeCollector != null) {
            sampleCollector.add(timeCollector.createPerformanceSample());
            timeCollector = null;
        }
        return true;
    }

    /** Returns the performance statistics. */
    public PerformanceHolder<PerformanceStats> getPerformance() {
        stop();
        final PerformanceStats stats =
                sampleCollector.createPerformanceStats(true);
        dispatchToConsumers(getName(), stats);
        return new PerformanceHolder<>(stats);
    }
}
