package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.DoubleValueExtractor;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CollectedMeasures<A extends SampleValueAccumulator> {

    private final TNameMap<A> accumulators;
    private final OnlineMeasure global = new OnlineMeasure();
    private final MultiMeasure multiMeasure;
    private final Map<TName, DimensionalMeasure> measures = new LinkedMap<>();

    public CollectedMeasures(TNameMap<A> accumulators, Unit unit,
            ListFilter<Double> filter) {
        this.accumulators = new TNameMap<>(accumulators);
        this.accumulators.forEach((TName name, A a) -> {
            List<Double> list = a.getValues();
            List<Double> filtered =
                    filter.filter(list, DoubleValueExtractor.INSTANCE);
            DimensionalOnlineMeasure measure =
                    new DimensionalOnlineMeasure(unit, filtered);
            a.setMeasure(measure);
            measures.put(name, measure);
            global.addAll(filtered);
        });
        multiMeasure = new MultiMeasure(global, measures.values());
    }

    public TNameMap<A> getAccumulators() {
        return accumulators;
    }

    public OnlineMeasure getGlobalMeasure() {
        return global;
    }

    public Map<TName, DimensionalMeasure> getMeasures() {
        return Collections.unmodifiableMap(measures);
    }

    public MultiMeasure getMultiMeasure() {
        return multiMeasure;
    }

    public <T> List<T> getSingleStatsList(Function<A,T> mapper) {
        List<T> list = new ArrayList<>();
        for (A a : accumulators.values()) {
            T singleStats = mapper.apply(a);
            list.add(singleStats);
        }
        return list;
    }
}
