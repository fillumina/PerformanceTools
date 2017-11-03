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
public class AllocatedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public AllocatedMemStats(MultiMeasureSignificance multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public AllocatedMemStats join(Stats<SingleStats> other) {
        Joiner<SingleStats> joiner = new Joiner<>(this, other);
        return new AllocatedMemStats(joiner.getMultiMeasure(), joiner.getMap());
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.ALLOCATED_INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryEvaluatorInfo.INSTANCE.getDebugString();
    }
}
