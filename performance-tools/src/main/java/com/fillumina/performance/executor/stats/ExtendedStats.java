package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates a new Stats including calculated results from expressions.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExtendedStats extends Stats {
    private static final long serialVersionUID = 1L;

    private final ExpressionSolver expression;

    public ExtendedStats(Stats other, ExpressionSolver expression) {
        this(other.getStatsType(), other.getMeasureMap(), expression);
    }

    public ExtendedStats(ExtendedStats other) {
        this(other.getStatsType(), other.getMeasureMap(), other.expression);
    }

    public ExtendedStats(StatsType type,
            Map<TName, DimensionalMeasure> measures,
            ExpressionSolver expression) {
        super(type, createMixedMap(type, measures, expression));
        this.expression = expression;
    }

    public Map<TName,String> getExpressionsAsString() {
        return expression.getStringExpressions();
    }

    private static Map<TName,DimensionalMeasure> createMixedMap(
            StatsType type,
            Map<TName, DimensionalMeasure> measures,
            ExpressionSolver expression) {
        if (measures.isEmpty()) {
            throw new RuntimeException("measures cannot be empty");
        }
        Unit<?> unit = measures.values().iterator().next().getUnit();
        Map<TName,DimensionalMeasure> map = new LinkedHashMap<>(measures);
        expression.solve(new Stats(type, measures))
                .forEach((CharSequence s, Measure m) ->
                    map.put(TN.tname(s), new DimensionalOnlineMeasure(unit, m)) );
        return Collections.unmodifiableMap(map);
    }
}
