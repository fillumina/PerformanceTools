package com.fillumina.performance.mem.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mem.sample.MemoryEvaluatorInfo;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStats extends Stats<SingleStats>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    public MemStats(MultiMeasureSignificance multiMeasure, TNameMap<SingleStats> map) {
        super(multiMeasure, map);
    }

    /** Must be overridden by subclasses. */
    @Override
    public MemStats join(Stats<SingleStats> other) {
        Joiner<SingleStats> joiner = new Joiner<>(this, other);
        return new MemStats(joiner.getMultiMeasure(), joiner.getMap());
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryEvaluatorInfo.INSTANCE.getDebugString();
    }
}
