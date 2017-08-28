package com.fillumina.performance.time.stats;

import com.fillumina.performance.infrastructure.ConsumerNotifierImpl;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.time.sample.TimeSampleBuilderImpl;
import com.fillumina.performance.util.tname.TName;

/**
 * Extracts performances out of an existing code with a stopwatch timer
 * paradigm.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimer
        extends ConsumerNotifierImpl<StopWatchTimer> {

    private final TimeSampleMultiCollector sampleMultiCollector;
    private TimeSampleBuilderImpl timeCollector;
    private long last;

    public StopWatchTimer() {
        this(new TimeSampleMultiCollector(TName.ROOT, true));
    }

    public StopWatchTimer(TimeSampleMultiCollector sampleMultiCollector) {
        this.sampleMultiCollector = sampleMultiCollector;
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
        if (timeCollector != null) {
            sampleMultiCollector.add(timeCollector.createPerformanceSample());
        }
        timeCollector = new TimeSampleBuilderImpl();
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
        timeCollector.add(TN.tname(name), segment, iteration);
        last = System.nanoTime();
        return true;
    }

    /** Stop the timer. It must be called at the end of each iteration. */
    public boolean stop() {
        if (timeCollector != null) {
            sampleMultiCollector.add(timeCollector.createPerformanceSample());
            timeCollector = null;
        }
        return true;
    }

    /** Returns the performance statistics. */
    public MixedAssertableHolder getPerformances() {
        stop();
        final MixedAssertableHolder stats =
                sampleMultiCollector.getMixedAssertableHolder();

        for (AssertableHolder<?> holder : stats.getStatsMap().values()) {
            dispatchToConsumers(holder.getAssertable());
        }

        return stats;
    }
}
