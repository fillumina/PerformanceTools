package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public UsedMemStats(MultiMeasureSignificance multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public UsedMemStats join(Stats<SingleStats> other) {
        Joiner<SingleStats> joiner = new Joiner<>(this, other);
        return new UsedMemStats(joiner.getMultiMeasure(), joiner.getMap());
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.USED_INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryEvaluatorInfo.INSTANCE.getDebugString();
    }
}
