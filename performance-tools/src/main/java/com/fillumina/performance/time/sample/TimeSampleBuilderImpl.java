package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Collects {@link IterationTime}s and creates a {@link AverageTimeSample}
 * out of them.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleBuilderImpl implements TimeSampleBuilder {
    private final Map<TName, IterationTimeAccumulator> timeMap;
    private long totalTimeNs;

    public TimeSampleBuilderImpl() {
        this.timeMap = new LinkedHashMap<>();
    }

    public TimeSampleBuilderImpl add(final TName name,
            final long elapsed, final int iterations) {
        IterationTimeAccumulator acc = timeMap.get(name);
        if (acc == null) {
            acc = new IterationTimeAccumulator();
            timeMap.put(name, acc);
        }
        acc.add(elapsed, iterations);
        totalTimeNs += elapsed;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return timeMap.isEmpty();
    }

    @Override
    public long getTotalTimeNs() {
        return totalTimeNs;
    }

    @Override
    public AverageTimeSample buildAverageTimeSample() {
        TNameMap<TimeSampleValue> map = createMap(
                ita -> 1.0 * ita.getIterations() / ita.getTimeNs());
        return new AverageTimeSample(map, totalTimeNs);
    }

    @Override
    public ThroughputSample buildThroughputSample() {
        TNameMap<TimeSampleValue> map = createMap(
                ita -> 1.0 * ita.getTimeNs() / ita.getIterations());
        return new ThroughputSample(map, totalTimeNs);
    }

    private TNameMap<TimeSampleValue> createMap(
            Function<IterationTimeAccumulator, Double> valueFunc) {
        TNameMap<TimeSampleValue> map = new TNameMap<>();
        for (Map.Entry<TName,IterationTimeAccumulator> e : timeMap.entrySet()) {
            TName name = e.getKey();
            IterationTimeAccumulator ita = e.getValue();

            TimeSampleValue s = new TimeSampleValue(
                    name,
                    valueFunc.apply(ita),
                    AverageTimeUnit.NANOSECONDS,
                    ita.getIterations(),
                    ita.getTimeNs());
            map.put(s);
        }
        return map;
    }
}
