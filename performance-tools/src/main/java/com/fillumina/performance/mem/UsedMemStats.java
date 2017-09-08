package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemStats extends MemStats {
    private static final long serialVersionUID = 1L;

    public UsedMemStats(MultiMeasure multiMeasure,
            List<SingleStats> singleStatsList) {
        super(multiMeasure, singleStatsList);
    }

}
