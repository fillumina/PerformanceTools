package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.ExpressionSolver;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpressionStatsProducer extends
        AbstractStatsProducerInstrumenter<ExpressionStatsProducer> {

    public interface Configuration {
        ExpressionSolver getExpressionSolver();
    }

    private final ExpressionSolver expressions;

    public ExpressionStatsProducer(Configuration configuration) {
        this.expressions = configuration.getExpressionSolver();
    }

    public ExpressionStatsProducer(ExpressionSolver expressions) {
        this.expressions = expressions;
    }

    @Override
    public MixedStatsHolder get() {
        getProducer().clearAndAddAllTests(getTests());
        if (expressions.getStringExpressions().isEmpty()) {
            return getProducer().get();
        }
        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        MixedStatsHolder mixedHolder = getProducer().get();

        mixedHolder.getStatsMap().forEach((StatsType type, StatsHolder holder) -> {
            Map<List<PathName>,Stats> map = new LinkedHashMap<>();
            holder.getTree().flattenTo(map);
            map.forEach((List<PathName> list, Stats stats) -> {
                if (stats != null) {
                    PathName name = list.get(list.size() - 1);
                    Stats eStats =
                            new Stats(stats, createMixedMap(stats, expressions));
                    eStats.putPayload(expressions);
                    builder.addStats(name, eStats);
                }
            });
        });
        return builder.build();
    }

    private static Map<PathName,DimensionalMeasure> createMixedMap(
            Stats stats,
            ExpressionSolver expression) {
        StatsType type = stats.getStatsType();
        Map<PathName, DimensionalMeasure> measures = stats.getMeasureMap();

        if (measures.isEmpty()) {
            throw new RuntimeException("measures cannot be empty");
        }
        Unit<?> unit = measures.values().iterator().next().getUnit();
        Map<PathName,DimensionalMeasure> map = new LinkedHashMap<>(measures);
        expression.solve(new Stats(stats, measures))
                .forEach((CharSequence s, Measure m) ->
                    map.put(PN.pname(s), new OnlineDimensionalMeasure(unit, m)) );
        return Collections.unmodifiableMap(map);
    }

}
