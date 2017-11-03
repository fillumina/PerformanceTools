package com.fillumina.performance.mock;

import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsMock extends Stats<SingleStats> {
    private static final long serialVersionUID = 1L;

    public static StatsMockBuilder builder() {
        return new StatsMockBuilder();
    }

    public StatsMock(MultiMeasureSignificance multiMeasure,
            TNameMap<SingleStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
    }
}
