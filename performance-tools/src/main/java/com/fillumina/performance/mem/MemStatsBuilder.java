package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.tname.TName;
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

    private final MemConsumptionExecutor executor;
    private final Map<TName, SingleMemStats> map;

    public MemStatsBuilder(MemConsumptionExecutor executor, int size) {
        this.executor = executor;
        this.map = new LinkedHashMap<>(size);
    }

    public void add(TName testName, Measure measure) {
        map.put(testName, new SingleMemStats(testName, measure));
    }

    @Override
    public MemStats build() {
        Measure lesserMem = calculateLesserMem().getUsedMemory();
        for (SingleMemStats mp : map.values()) {
            mp.setRatio(new MeasureRatio(mp.getUsedMemory(), lesserMem,
                    Ratio.P_99));
        }
        return executor.createStats(Collections.unmodifiableMap(map));
    }

    private SingleMemStats calculateLesserMem() {
        SingleMemStats lesserMp = null;
        double lesserMean = Double.POSITIVE_INFINITY;
        for (SingleMemStats mp : map.values()) {
            double mean = mp.getUsedMemory().getMean();
            if (mean < lesserMean) {
                lesserMean = mean;
                lesserMp = mp;
            }
        }
        return lesserMp;
    }
}
