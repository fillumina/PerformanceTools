package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.unit.AverageTimeUnit;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.ThroughputUnit;
import com.fillumina.performance.util.unit.Unit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Collects iterations times and creates two kind of samples out of them:
 * <ol>
 * <li>average duration;
 * <li>throughput: iterations per second.
 * </ol>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleCollector implements TimeSampleBuilder {
    private final Map<PathName, IterationTimeAccumulator> timeMap;
    private long totalTimeNs;

    public TimeSampleCollector() {
        this.timeMap = new LinkedHashMap<>();
    }

    public TimeSampleCollector add(
            final PathName name,
            final long elapsed,
            final int iterations) {
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
        PathNamedMap<SampleValue> map = createMap(
                AverageTimeUnit.NANOSECONDS,
                ita -> 1.0 * ita.getTimeNs() / ita.getIterations());
        return new Sample(TimeStatsType.AVERAGE, map);
    }

    @Override
    public Sample buildThroughputSample() {
        PathNamedMap<SampleValue> map = createMap(
                ThroughputUnit.OP,
                ita -> 1E9 * ita.getIterations() / ita.getTimeNs());
        return new Sample(TimeStatsType.THROUGHPUT, map);
    }

    private PathNamedMap<SampleValue> createMap(
            Unit<?> unit,
            Function<IterationTimeAccumulator, Double> valueFunc) {
        PathNamedMap<SampleValue> map = new PathNamedMap<>(timeMap.size());
        timeMap.entrySet().forEach((e) -> {
            PathName name = e.getKey();
            IterationTimeAccumulator ita = e.getValue();

            SampleValue s = new TimeSampleValue(
                    name,
                    Quantity.from(valueFunc.apply(ita), unit),
                    ita.getIterations(),
                    ita.getTimeNs());

            map.put(name, s);
        });
        return map;
    }
}
