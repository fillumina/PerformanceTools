package com.fillumina.performance.mem.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import com.fillumina.performance.util.stats.Significance;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStats extends Stats<SingleStats>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    public MemStats(Significance multiMeasure, TNameMap<SingleStats> map) {
        super(multiMeasure, map);
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryEvaluatorInfo.INSTANCE.getDebugString();
    }
}
