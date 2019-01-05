package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.ExpressionSolver;
import com.fillumina.performance.executor.stats.ExtendedStats;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.tname.TName;
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
            Map<List<TName>,Stats> map = new LinkedHashMap<>();
            holder.getTree().flattenTo(map);
            map.forEach( (List<TName> list, Stats stats) -> {
                if (stats != null) {
                    TName name = list.get(list.size() - 1);
                    ExtendedStats eStats = new ExtendedStats(stats, expressions);
                    builder.addStats(name, eStats);
                }
            });
        });
        return builder.build();
    }
}
