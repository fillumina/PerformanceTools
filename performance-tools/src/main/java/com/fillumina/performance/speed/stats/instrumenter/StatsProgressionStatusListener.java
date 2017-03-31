package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.StaticPath;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProgressionStatusListener {

    void acceptStatsProgressionStatus(StaticPath name,
            SpeedStats stats, String rejectionMessage);
}
