package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.MultiMeasure;
import java.util.LinkedHashMap;

/**
 * Statistics about the average time (average time per operation) of a group of
 * tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AverageTimeStats extends TimeStats {
    private static final long serialVersionUID = 1L;

    public AverageTimeStats(MultiMeasure multiMeasure,
            LinkedHashMap<TName, SingleTimeStats> testStatsMap) {
        super(multiMeasure, testStatsMap);
    }

    @Override
    public AverageTimeStats add(SingleTimeStats single) {
        MultiMeasure jointMm = MultiMeasure.add(getMultiMeasure(),
                single.getMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.put(single.getName(), single);
        return new AverageTimeStats(jointMm, map);
    }

    @Override
    public AverageTimeStats join(TimeStats b) {
        MultiMeasure jointMm =
                MultiMeasure.join(getMultiMeasure(), b.getMultiMeasure());
        LinkedHashMap<TName,SingleTimeStats> map = new LinkedHashMap<>();
        map.putAll(getTestStatsMap());
        map.putAll(b.getTestStatsMap());
        return new AverageTimeStats(jointMm, map);
    }

    @Override
    public String toString() {
        return TimeStatsStringGeneratorSelector.AVERAGE_TIME.toString(this);
    }
}
