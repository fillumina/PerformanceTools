package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.LinkedHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public SpeedStats(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        super(multiMeasure, testStatsMap);
    }

    @Override
    public SpeedStats add(SingleTimeStats single) {
        MultiMeasure jointMm = MultiMeasure.add(getMultiMeasure(),
                single.getElapsedNanosecondsPerCycle());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.put(single.getName(), single);
        return new SpeedStats(jointMm, map);
    }

    @Override
    public SpeedStats join(TimeStats b) {
        MultiMeasure jointMm =
                MultiMeasure.join(getMultiMeasure(), b.getMultiMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.putAll(b.getTestStatsMap());
        return new SpeedStats(jointMm, map);
    }

    //TODO complete toString()
}
