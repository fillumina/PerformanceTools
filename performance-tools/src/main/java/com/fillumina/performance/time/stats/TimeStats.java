package com.fillumina.performance.time.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;

/**
 * Statistics about the experiment.
 * In addition of the usual statistics it calculates ANOVA and performs the
 * Tukey HSD post-hoc test on all experiment pairs so to assess the data
 * collected as statistically significant.
 * <p>
 * This class is immutable.
 *
 * @author Francesco Illuminati
 */
public class TimeStats extends Stats<SingleTimeStats>
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final long totalTimeNs;

    public TimeStats(MultiMeasure multiMeasure,
            TNameMap<SingleTimeStats> singleStatsMap) {
        super(multiMeasure, singleStatsMap);
        totalTimeNs = calculateTotalTimeNs(singleStatsMap);
    }

    public long getTotalTimeNs() {
        return totalTimeNs;
    }

    private long calculateTotalTimeNs(TNameMap<SingleTimeStats> singleStatsMap) {
        long totalTimeNs = 0;
        for (SingleTimeStats s : singleStatsMap.values()) {
            totalTimeNs += s.getTotalTime();
        }
        return totalTimeNs;
    }
}
