package com.fillumina.performance.executor.progression;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.tname.TName;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface StatsProgressionStatusListener {

    void acceptWarmupProgressionStatus(TName name, double speed);

    void acceptStatsProgressionStatus(TName name,
            Collection<? extends Stats<?>> stats,
            String rejectionMessage);
}
