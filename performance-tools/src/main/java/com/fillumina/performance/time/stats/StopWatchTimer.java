package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.stats.ReciprocalOnlineMeasureSampler;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ThroughputUnit;
import java.util.Map;

/**
 * Extracts performances out of an existing code using a stopwatch timer.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimer {

    private final IndexedArrayMap<String,ReciprocalOnlineMeasureSampler> map;
    private final ReciprocalOnlineMeasureSampler[] cache;
    private long last;

    public StopWatchTimer() {
        this(16);
    }

    public StopWatchTimer(int size) {
        map = new IndexedArrayMap<>(size);
        cache = new ReciprocalOnlineMeasureSampler[size];
        for (int i=0; i<size; i++) {
            cache[i] = new ReciprocalOnlineMeasureSampler();
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
        Map<TName,DimensionalMeasure> avgMap = new IndexedArrayMap<>(map.size());
        Map<TName,DimensionalMeasure> tptMap = new IndexedArrayMap<>(map.size());

        map.forEach( (s,m) -> {
                TName tname = TN.tname(s);
                avgMap.put(tname,
                        new DefaultDimensionalMeasure(
                                m.getDirect(),
                                AverageTimeUnit.NANOSECONDS));
                tptMap.put(tname,
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
