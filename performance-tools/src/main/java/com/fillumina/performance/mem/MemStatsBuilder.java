package com.fillumina.performance.mem;

import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class MemStatsBuilder implements Builder<MemStats> {

    private final Map<TName, MemPerformance> map;

    public MemStatsBuilder(int size) {
        this.map = new LinkedHashMap<>(size);
    }

    public void add(TName testName, Measure measure) {
        map.put(testName, new MemPerformance(testName, measure));
    }

    @Override
    public MemStats build() {
        Measure lesserMem = calculateLesserMem().getUsedMemory();
        for (MemPerformance mp : map.values()) {
            mp.setRatio(new MeasureRatio(mp.getUsedMemory(), lesserMem,
                    Ratio.P_99));
        }
        return new MemStats(Collections.unmodifiableMap(map));
    }

    private MemPerformance calculateLesserMem() {
        MemPerformance lesserMp = null;
        double lesserMean = Double.POSITIVE_INFINITY;
        for (MemPerformance mp : map.values()) {
            double mean = mp.getUsedMemory().getMean();
            if (mean < lesserMean) {
                lesserMean = mean;
                lesserMp = mp;
            }
        }
        return lesserMp;
    }
}
