package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsBuilder implements StatsTyped {

    private static class Accumulator implements TNamed {
        TName name;
        StatsType type;
        List<Double> values = new ArrayList<>();
        long totalIterations;
        long totalTime;

        @Override
        public TName getName() {
            return name;
        }
    }

    private final TNameMap<Accumulator> accumulators = new TNameMap<>();
    private final StatsType type;
    private Unit<?> unit;

    public StatsBuilder(StatsType type) {
        this.type = type;
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public void addSample(Sample sample) {
        sample.getValuesMap().values().forEach((SampleValue v) -> {
            if (unit == null) {
                unit = v.getUnit();
            }
            Accumulator acc = getAccumulator(v.getName());
            acc.name = v.getName();
            acc.totalIterations += v.getIterations();
            acc.totalTime += v.getTimeNs();
            acc.values.add(v.getValue());
        });
    }

    /**
     * Builds a {@link Stats} out of the collected samples.
     *
     * @param message       The message to addSample to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    public Stats createStats(ListFilter<Double> filter) {
        Map<TName, DimensionalMeasure> map = new ArrayMap<>();

        this.accumulators.values().forEach((Accumulator acc) -> {
            List<Double> filtered = filter.filter(acc.values);
            DimensionalOnlineMeasure measure =
                    new DimensionalOnlineMeasure(unit, filtered);
            map.put(acc.name, measure);
        });

        return new Stats(type, map);
    }

    private Accumulator getAccumulator(TName name) {
        Accumulator a = accumulators.get(name);
        if (a == null) {
            a = new Accumulator();
            accumulators.put(name, a);
        }
        return a;
    }
}
