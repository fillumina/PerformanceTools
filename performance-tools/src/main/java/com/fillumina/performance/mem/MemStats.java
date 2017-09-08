package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStats extends Stats<SingleStats>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    public MemStats(MultiMeasure multiMeasure, List<SingleStats> singleStatsList) {
        super(multiMeasure, singleStatsList);
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryAllocatorInfo.INSTANCE.getDebugString();
    }
}
