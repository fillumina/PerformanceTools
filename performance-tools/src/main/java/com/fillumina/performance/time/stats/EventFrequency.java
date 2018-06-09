package com.fillumina.performance.time.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTypeImpl;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.Looper;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.ReciprocalOnlineMeasureSampler;
import com.fillumina.performance.util.stats.SingleMeasure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.ThroughputUnit;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Extracts performances out of an existing code using a stopwatch timer.
 *
 * @see com.fillumina.performance.Telemetry
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EventFrequency {
    public static final StatsType FREQUENCY = new StatsTypeImpl("frequency");

    private static class Event {
        private final ReciprocalOnlineMeasureSampler sampler =
                new ReciprocalOnlineMeasureSampler();
        private long last;

        public void fire() {
            long now = System.nanoTime();
            sampler.addSample(now - last);
            last = System.nanoTime();
        }

        public void clear() {
            sampler.clear();
        }

        public ReciprocalOnlineMeasureSampler getSampler() {
            return sampler;
        }
    }

    private final IndexedHashMap<String,Event> map;
    private final Event[] events;

    public EventFrequency() {
        this(16);
    }

    public EventFrequency(String... names) {
        this(names.length);
        init(names);
    }

    public EventFrequency(int size) {
        map = new IndexedHashMap<>(size);
        events = new Event[size];
        for (int i=0; i<size; i++) {
            events[i] = new Event();
        }
    }

    private void clear() {
        for (Event e : events) {
            e.clear();
        }
    }

    /**
     * Pre-load {@link ReciprocalOnlineMeasureSampler} so to avoid
     * the first-time call initialization problem.
     *
     * @param names test names that will be pre-initialized.
     */
    public EventFrequency init(String... names) {
        for (String n : names) {
            Event sampler = map.get(n);
            if (sampler == null) {
                sampler = events[map.size()];
                map.put(n, sampler);
            }
        }
        return this;
    }

    /**
     * It's advisable to use warmup with
     * {@link #warmupAndLoop(Quantity, Quantity, Consumer) }
     * instead of this method if it is possible.
     */
    public MixedStatsHolder loop(Quantity<IntervalUnit> time,
            Consumer<EventFrequency> consumer) {
        return warmupAndLoop(null, time, consumer);
    }

    public MixedStatsHolder warmupAndLoop(
            Quantity<IntervalUnit> warmupInterval,
            Quantity<IntervalUnit> testInterval,
            Consumer<EventFrequency> consumer) {
        clear();
        if (warmupInterval != null && warmupInterval.getValue() > 0) {
            Looper.loop(warmupInterval, this, consumer);
            clear();
        }
        Looper.loop(testInterval, this, consumer);
        return getPerformances();
    }

    /**
     * It's advisable to use warmup with
     * {@link #warmupAndLoop(int, int, Consumer) }
     * instead of this method if it is possible.
     */
    public MixedStatsHolder loop(int times, Consumer<EventFrequency> consumer) {
        return warmupAndLoop(0, times, consumer);
    }

    public MixedStatsHolder warmupAndLoop(
            int warmupIterations, int testIterations,
            Consumer<EventFrequency> consumer) {
        clear();
        if (warmupIterations > 0) {
            Looper.loop(testIterations, this, consumer);
            clear();
        }
        Looper.loop(testIterations, this, consumer);
        return getPerformances();
    }

    /** Starts the timer. It must be called at each new iteration. */
    public boolean start() {
        return true;
    }

    /**
     * Accounts the time elapsed since the call to {@link #start()} or the
     * last call to {@link #fire(String)} to named section specifying
     * how many iterations the code has completed.
     */
    public boolean fire(final String name) {
        Event event = map.get(name);
        if (event == null) {
            event = events[map.size()];
            map.put(name, event);
        }
        event.fire();
        return true;
    }

    /**
     * Accounts the time elapsed since the call to {@link #start()} or the
     * last call to {@link #fire(String)} to named section specifying
     * how many iterations the code has completed.
     * It is useful to avoid the extra time spent by looking into the map.
     */
    public boolean fire(int index) {
        events[index].fire();
        return true;
    }

    /** Returns the performance statistics. */
    public MixedStatsHolder getPerformances() {
        return getPerformances(OutlierEliminatorFilter.INSTANCE);
    }

    /** Returns the performance statistics. */
    public MixedStatsHolder getPerformances(ListFilter<Double> filter) {
        Map<TName,DimensionalMeasure> cntMap = new IndexedHashMap<>(map.size());
        Map<TName,DimensionalMeasure> avgMap = new IndexedHashMap<>(map.size());
        Map<TName,DimensionalMeasure> tptMap = new IndexedHashMap<>(map.size());

        map.forEach( (s,m) -> {
                TName tname = TN.tname(s);
                Measure direct = m.getSampler().getDirect();
                Measure inverse = m.getSampler().getInverse();
                cntMap.put(tname,
                        new DefaultDimensionalMeasure(
                                new SingleMeasure(direct.getCount()),
                                Magnitude.UNIT));
                avgMap.put(tname,
                        new DefaultDimensionalMeasure(
                                direct,
                                AverageTimeUnit.NANOSECONDS));
                tptMap.put(tname,
                        new DefaultDimensionalMeasure(
                                inverse,
                                ThroughputUnit.GIGAOP));
        });

        Stats cntStats = new Stats(FREQUENCY, cntMap);
        Stats avgStats = new Stats(TimeStatsType.AVERAGE, avgMap);
        Stats tptStats = new Stats(TimeStatsType.THROUGHPUT, tptMap);

        StatsHolder cntHolder = new StatsHolder(cntStats);
        StatsHolder avgHolder = new StatsHolder(avgStats);
        StatsHolder tptHolder = new StatsHolder(tptStats);

        return new MixedStatsHolder(cntHolder, avgHolder, tptHolder);
    }
}
