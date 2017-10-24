package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import com.fillumina.performance.util.stats.Significance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public AllocatedMemStats(Significance multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.ALLOCATED_INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryEvaluatorInfo.INSTANCE.getDebugString();
    }
}
