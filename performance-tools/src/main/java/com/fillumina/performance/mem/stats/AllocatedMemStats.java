package com.fillumina.performance.mem.stats;

import com.fillumina.performance.stats.SingleStats;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public AllocatedMemStats(MultiMeasure multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }

}
