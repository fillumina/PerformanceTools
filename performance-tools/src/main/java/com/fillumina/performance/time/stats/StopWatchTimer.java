package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.StatsCreator;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.ConsumerNotifierImpl;
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
        extends ConsumerNotifierImpl<StopWatchTimer, Sample> {

    private final ListFilter<Double> filter;
    private TimeSampleCollector collector;
    private StatsCreator creator;
    private long last;

    public StopWatchTimer() {
        this(OutlierEliminatorFilter.INSTANCE);
    }

    public StopWatchTimer(ListFilter<Double> filter) {
        this.filter = filter;
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
        recordSamples();
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

    private void recordSamples() {
        if (collector != null) {
            if (creator == null) {
                creator = new StatsCreator(TName.ROOT);
            }
            creator.addSample(TimeStatsType.AVERAGE_TIME,
                    collector.buildAverageTimeSample());
            creator.addSample(TimeStatsType.THROUGHPUT,
                    collector.buildThroughputSample());
        }
        collector = new TimeSampleCollector();
    }

    /** Returns the performance statistics. */
    public MixedAssertableHolder getPerformances() {
        recordSamples();
        MixedAssertableHolder mixedStats = creator.getMixedAssertableHolder(filter);
        collector = null;
        creator = null;
        return mixedStats;
    }
}
