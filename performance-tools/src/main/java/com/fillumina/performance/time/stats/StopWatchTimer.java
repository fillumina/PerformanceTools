package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.ConsumerNotifierImpl;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.infrastructure.stats.StatsCreator;
import com.fillumina.performance.time.sample.AbstractTimeSample;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.tname.TName;

/**
 * Extracts performances out of an existing code using a stopwatch timer.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimer
        extends ConsumerNotifierImpl<StopWatchTimer, AbstractTimeSample> {

    private final ListFilter<Double> filter;
    private TimeSampleCollector collector;
    private StatsCreator<TimeStats, AbstractTimeSample> creator;
    private long last;

    public StopWatchTimer() {
        this(OutlierEliminatorFilter.INSTANCE);
    }

    public StopWatchTimer(ListFilter<Double> filter) {
        this.filter = filter;
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
        if (collector == null) {
            collector = new TimeSampleCollector();
        }
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
    public boolean section(final String name, final int iterations) {
        final long segmentNs = System.nanoTime() - last;
        collector.add(TN.tname(name), segmentNs, iterations);
        last = System.nanoTime();
        return true;
    }

    /** Stop the timer. It must be called at the end of each iteration. */
    public boolean stop() {
        if (collector != null) {
            if (creator == null) {
                creator = new StatsCreator<>(TName.ROOT);
            }
            creator.addSample(collector.buildAverageTimeSample());
            creator.addSample(collector.buildThroughputSample());
            collector = null;
        }
        return true;
    }

    /** Returns the performance statistics. */
    public MixedAssertableHolder getPerformances() {
        stop();
        MixedAssertableHolder mixedStats = creator.getMixedAssertableHolder(filter);
        collector = null;
        creator = null;
        return mixedStats;
    }
}
