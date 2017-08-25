package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.tname.TName;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProgressionStatusListener {

    void acceptWarmupProgressionStatus(TName name, double speed);

    void acceptStatsProgressionStatus(TName name,
            Collection<TimeStats> stats,
            String rejectionMessage);
}
