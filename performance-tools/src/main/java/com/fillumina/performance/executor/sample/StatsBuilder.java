package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.stats.OnlineMeasure;
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
        Stats.Type type;
        List<Double> values = new ArrayList<>();
        long totalIterations;
        long totalTime;

        @Override
        public TName getName() {
            return name;
        }
    }

    private final TNameMap<Accumulator> accumulators = new TNameMap<>();
    private final Stats.Type type;
    private Unit<?> unit;

    public StatsBuilder(Stats.Type type) {
        this.type = type;
    }

    @Override
    public Stats.Type getStatsType() {
        return type;
    }

    /**
     * Builds a {@link Stats} out of the collected samples.
     *
     * @param message       The message to addSample to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    public Stats createStats(ListFilter<Double> filter) {
        TNameMap<SingleStats> singleStatsList = new TNameMap<>();
        Map<TName, DimensionalMeasure> measures = new LinkedMap<>();
        OnlineMeasure global = new OnlineMeasure();

        this.accumulators.values().forEach((Accumulator acc) -> {
            List<Double> filtered = filter.filter(acc.values);
            DimensionalOnlineMeasure measure =
                    new DimensionalOnlineMeasure(unit, filtered);
            measures.put(acc.name, measure);
            singleStatsList.add(new SingleStats(acc.name, measure,
                                    acc.totalIterations,
                                    acc.values.size(),
                                    acc.totalTime));
            global.addAll(filtered);
        });

        MultiMeasureSignificance significance =
                new MultiMeasureSignificance(global, measures.values());

        return new Stats(type, significance, singleStatsList);
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

    private Accumulator getAccumulator(TName name) {
        Accumulator a = accumulators.get(name);
        if (a == null) {
            a = new Accumulator();
            accumulators.put(name, a);
        }
        return a;
    }
}
