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

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreator implements StatsTyped {

    private final ArrayMap<TName, List<Double>> valuesMap = new ArrayMap<>();
    private final StatsType type;
    private Unit<?> unit;

    public StatsCreator(StatsType type) {
        this.type = type;
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public void addSample(Sample sample) {
        sample.getValuesMap().values().forEach((SampleValue v) -> {
            if (unit == null) {
                unit = v.getQuantity().getUnit().units().getBase();
            }
            List<Double> list = getValueList(v.getName());
            list.add(v.getQuantity().toBase());
        });
    }

    public Stats createStats() {
        return createStats(ListFilter.<Double>identity());
    }

    /**
     * Builds a {@link Stats} out of the collected samples.
     */
    public Stats createStats(ListFilter<Double> filter) {
        ArrayMap<TName, DimensionalMeasure> map = new ArrayMap<>();

        valuesMap.forEach((TName name, List<Double> list) -> {
            List<Double> filtered = filter.filter(list);
            DimensionalOnlineMeasure measure =
                    new DimensionalOnlineMeasure(unit, filtered);
            map.put(name, measure);
        });

        return new Stats(type, map);
    }

    private List<Double> getValueList(TName name) {
        List<Double> a = valuesMap.get(name);
        if (a == null) {
            a = new ArrayList<>();
            valuesMap.put(name, a);
        }
        return a;
    }
}
