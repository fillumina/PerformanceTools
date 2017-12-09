package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.ThroughputUnit;
import com.fillumina.performance.util.unit.Unit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Collects {@link IterationTime}s and creates a {@link AverageTimeSample}
 * out of them.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleCollector implements TimeSampleBuilder {
    private final Map<TName, IterationTimeAccumulator> timeMap;
    private long totalTimeNs;

    public TimeSampleCollector() {
        this.timeMap = new LinkedHashMap<>();
    }

    public TimeSampleCollector add(final TName name,
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
    public Sample buildAverageTimeSample() {
        TNameMap<SampleValue> map = createMap("average time",
                AverageTimeUnit.NANOSECONDS,
                ita -> 1.0 * ita.getTimeNs() / ita.getIterations());
        return new Sample(TimeStatsType.AVERAGE, map);
    }

    @Override
    public Sample buildThroughputSample() {
        TNameMap<SampleValue> map = createMap("throughput",
                ThroughputUnit.OP,
                ita -> 1E9 * ita.getIterations() / ita.getTimeNs());
        return new Sample(TimeStatsType.THROUGHPUT, map);
    }

    private TNameMap<SampleValue> createMap(String type,
            Unit<?> unit,
            Function<IterationTimeAccumulator, Double> valueFunc) {
        TNameMap<SampleValue> map = new TNameMap<>(timeMap.size());
        for (Map.Entry<TName,IterationTimeAccumulator> e : timeMap.entrySet()) {
            TName name = e.getKey();
            IterationTimeAccumulator ita = e.getValue();

            SampleValue s = new TimeSampleValue(
                    name,
                    Quantity.from(valueFunc.apply(ita), unit),
                    type,
                    ita.getIterations(),
                    ita.getTimeNs());

            map.put(name, s);
        }
        return map;
    }
}
