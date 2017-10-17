package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public UsedMemStats(MultiMeasure multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.USED_INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryAllocatorInfo.INSTANCE.getDebugString();
    }
}
