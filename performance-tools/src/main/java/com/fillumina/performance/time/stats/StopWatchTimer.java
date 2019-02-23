package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.ReciprocalOnlineMeasureSampler;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ThroughputUnit;
import java.util.Map;

/**
 * Extracts performances using a stopwatch timer.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimer {

    private final IndexedHashMap<String,ReciprocalOnlineMeasureSampler> map;
    private final ReciprocalOnlineMeasureSampler[] cache;
    private long last;

    public StopWatchTimer() {
        this(16);
    }

    public StopWatchTimer(String... names) {
        this(names.length);
        init(names);
    }

    public StopWatchTimer(int size) {
        map = new IndexedHashMap<>(size);
        cache = new ReciprocalOnlineMeasureSampler[size];
        for (int i=0; i<size; i++) {
            cache[i] = new ReciprocalOnlineMeasureSampler();
        }
    }

    /**
     * Pre-load {@link ReciprocalOnlineMeasureSampler} so to avoid
     * the first-time call initialization problem.
     *
     * @param names test names that will be pre-initialized.
     */
    public StopWatchTimer init(String... names) {
        for (String n : names) {
            ReciprocalOnlineMeasureSampler sampler = map.get(n);
            if (sampler == null) {
                sampler = cache[map.size()];
                map.put(n, sampler);
            }
        }
        return this;
    }

    public void reset() {
        for (ReciprocalOnlineMeasureSampler s: cache) {
            s.clear();
        }
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
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
        ReciprocalOnlineMeasureSampler sampler = map.get(name);
        if (sampler == null) {
            sampler = cache[map.size()];
            map.put(name, sampler);
        }
        sampler.addSample(1.0 * segmentNs / iterations);
        last = System.nanoTime();
        return true;
    }

    /** Returns the performance statistics. */
    public MixedStatsHolder getPerformances() {
        return getPerformances(OutlierEliminatorFilter.INSTANCE);
    }

    /** Returns the performance statistics. */
    public MixedStatsHolder getPerformances(ListFilter<Double> filter) {
        Map<PathName,DimensionalMeasure> avgMap = new IndexedHashMap<>(map.size());
        Map<PathName,DimensionalMeasure> tptMap = new IndexedHashMap<>(map.size());

        map.forEach((s,m) -> {
                PathName pname = PN.pname(s);
                avgMap.put(pname,
                        new DefaultDimensionalMeasure(
                                m.getDirect(),
                                AverageTimeUnit.NANOSECONDS));
                tptMap.put(pname,
                        new DefaultDimensionalMeasure(
                                m.getInverse(),
                                ThroughputUnit.GIGAOP));
        });

        Stats avgStats = new Stats(TimeStatsType.AVERAGE, avgMap);
        Stats tptStats = new Stats(TimeStatsType.THROUGHPUT, tptMap);

        StatsHolder avgHolder = new StatsHolder(avgStats);
        StatsHolder tptHolder = new StatsHolder(tptStats);

        return new MixedStatsHolder(avgHolder, tptHolder);
    }
}
