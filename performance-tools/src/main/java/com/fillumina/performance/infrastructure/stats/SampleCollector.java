package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.time.stats.*;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.DoubleValueExtractor;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SampleCollector<S extends Stats<T>,
                                   T extends SingleStats,
                                   A extends AbstractSample<A,V,S,T>,
                                   V extends SampleValue> {

    private final List<A> samples = new ArrayList<>();
    private final Map<TName, List<Double>> values = new LinkedMap<>();
    private Unit unit;

    protected abstract S createNewStats(Measures measures);

    public void addSample(A sample) {
        samples.add(sample);
        sample.getTestSamples().forEach(t -> {
            getList(t.getTestName()).add(t.getValue());
            if (unit == null) {
                unit = t.getUnit();
            }
        });
    }

    private List<Double> getList(TName name) {
        List<Double> list = values.get(name);
        if (list == null) {
            list = new ArrayList<>();
            values.put(name, list);
        }
        return list;
    }

    /**
     * Builds a {@link TimeStats} out of the collected samples.
     *
     * @param message       The message to addSample to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    public S createStats(ListFilter<Double,Double> filter) {
        return createNewStats(new Measures(filter));
    }

    public class Measures {
        private final OnlineMeasure global = new OnlineMeasure();
        private Map<TName, DimensionalMeasure> measures = new LinkedMap<>();
        private MultiMeasure multiMeasure;

        private Measures(ListFilter<Double,Double> filter) {
            values.forEach((name, list) -> {
                List<Double> filtered =
                        filter.filter(list, DoubleValueExtractor.INSTANCE);
                measures.put(name, new DimensionalOnlineMeasure(unit, filtered));
                global.addAll(filtered);
            });
            multiMeasure = new MultiMeasure(global, measures.values());
        }

        public List<T> getSingleStatsList(
                BiFunction<TName, DimensionalMeasure, T> mapping) {
            List<T> list = new ArrayList<>();
            measures.forEach((name, measure) -> {
                list.add(mapping.apply(name, measure));
            });
            return list;
        }

        public List<A> getSamples() {
            return Collections.unmodifiableList(samples);
        }

        public Map<CharSequence,List<Double>> getRowValues() {
            return Collections.unmodifiableMap(values);
        }

        public OnlineMeasure getGlobal() {
            return global;
        }

        public Map<CharSequence, Measure> getMeasures() {
            return Collections.unmodifiableMap(measures);
        }

        public MultiMeasure getMultiMeasure() {
            return multiMeasure;
        }
    }
}
