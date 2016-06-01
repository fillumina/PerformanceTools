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

    private final PerformanceSampleCollector sampleCollector =
            new PerformanceSampleCollector();
    private IterationTimeCollector timeCollector;
    private long last;

    public boolean start() {
        if (timeCollector != null) {
            sampleCollector.add(timeCollector.createPerformanceSample());
        }
        timeCollector = new IterationTimeCollector();
        last = System.nanoTime();
        return true;
    }

    public boolean section(final String name) {
        return section(name, 1);
    }

    public boolean section(final String name, final int iteration) {
        final long segment = System.nanoTime() - last;
        timeCollector.add(name, segment, iteration);
        last = System.nanoTime();
        return true;
    }

    public boolean stop() {
        if (timeCollector != null) {
            sampleCollector.add(timeCollector.createPerformanceSample());
            timeCollector = null;
        }
        // TODO shouldn't dispatch here?
        return true;
    }

    public PerformanceHolder<PerformanceStats> getPerformance() {
        stop();
        final PerformanceStats stats =
                sampleCollector.createPerformanceStats(null, true);
        dispatchToConsumers(getName(), stats);
        return new PerformanceHolder<>(stats);
    }
}
