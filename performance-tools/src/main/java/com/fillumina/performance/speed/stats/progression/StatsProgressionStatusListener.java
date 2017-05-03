package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProgressionStatusListener {

    void acceptStatsProgressionStatus(TName name,
            SpeedStats stats, String rejectionMessage);
}
