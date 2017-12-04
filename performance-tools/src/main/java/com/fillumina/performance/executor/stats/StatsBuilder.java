package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
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

    private final ArrayMap<TName, List<Double>> accumulators = new ArrayMap<>();
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
                unit = v.getQuantity().getUnit();
            }
            List<Double> list = getAccumulator(v.getName());
            list.add(v.getQuantity().toBase());
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

        this.accumulators.forEach((TName name, List<Double> list) -> {
            List<Double> filtered = filter.filter(list);
            DimensionalOnlineMeasure measure =
                    new DimensionalOnlineMeasure(unit, filtered);
            map.put(name, measure);
        });

        return new Stats(type, map);
    }

    private List<Double> getAccumulator(TName name) {
        List<Double> a = accumulators.get(name);
        if (a == null) {
            a = new ArrayList<>();
            accumulators.put(name, a);
        }
        return a;
    }
}
