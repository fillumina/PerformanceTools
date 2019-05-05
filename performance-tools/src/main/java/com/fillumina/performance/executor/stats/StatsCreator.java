package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.QuantityList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreator implements StatsTyped {

    private final IndexedHashMap<PathName, QuantityList.Builder> valuesMap =
            new IndexedHashMap<>();
    private final StatsType type;

    public StatsCreator(StatsType type) {
        this.type = type;
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public void addSample(Sample sample) {
        sample.getValuesMap().values().forEach((SampleValue v) -> {
            QuantityList.Builder builder = getBuilder(v.getPathName());
            builder.add(v.getQuantity());
        });
    }

    public Stats createStats() {
        return createStats(ListFilter.<Double>identity());
    }

    /**
     * Builds a {@link Stats} out of the collected samples.
     */
    public Stats createStats(ListFilter<Double> filter) {
        IndexedHashMap<PathName, DimensionalMeasure> map = new IndexedHashMap<>();

        valuesMap.forEach((PathName name, QuantityList.Builder builder) -> {
            final QuantityList qList = builder.build();
            List<Double> filtered = filter.filter(qList);
            OnlineDimensionalMeasure measure =
                    new OnlineDimensionalMeasure(qList.getUnit(), filtered);
            map.put(name, measure);
        });

        return new Stats(type, map);
    }

    private QuantityList.Builder getBuilder(PathName name) {
        QuantityList.Builder builder = valuesMap.get(name);
        if (builder == null) {
            builder = QuantityList.builder();
            valuesMap.put(name, builder);
        }
        return builder;
    }
}
