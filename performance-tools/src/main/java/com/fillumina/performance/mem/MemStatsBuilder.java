package com.fillumina.performance.mem;

import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsBuilder implements Builder<MemStats> {

    private final Map<String, MemPerformance> map;

    public MemStatsBuilder(int size) {
        this.map = new LinkedHashMap<>(size);
    }

    public void add(String testName, Measure measure) {
        map.put(testName, new MemPerformance(testName, measure));
    }

    @Override
    public MemStats build() {
        Measure slower = calculateSlower().getUsedMemory();
        for (MemPerformance mp : map.values()) {
            mp.setRatio(new MeasureRatio(mp.getUsedMemory(), slower, 0.99));
        }
        return new MemStats(Collections.unmodifiableMap(map));
    }

    private MemPerformance calculateSlower() {
        MemPerformance slower = null;
        double slowerMean = Double.POSITIVE_INFINITY;
        for (MemPerformance mp : map.values()) {
            double mean = mp.getUsedMemory().getMean();
            if (mean < slowerMean) {
                slowerMean = mean;
                slower = mp;
            }
        }
        return slower;
    }
}
