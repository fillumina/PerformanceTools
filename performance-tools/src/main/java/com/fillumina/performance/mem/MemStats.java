package com.fillumina.performance.mem;

import com.fillumina.performance.mem.sample.MemoryConsumptionStatus;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;
import java.util.Map;
import com.fillumina.performance.assertion.AssertableMultiStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStats implements AssertableMultiStats, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, MemPerformance> map;

    public MemStats(Map<String, MemPerformance> map) {
        this.map = map;
    }

    public Map<String, MemPerformance> getPerformances() {
        return map;
    }

    @Override
    public Measure getValue(String testName) {
        final MemPerformance performance = map.get(testName);
        if (performance == null) {
            throw new IllegalStateException("test '" + testName + "' not found");
        }
        return performance.getUsedMemory();
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        return map.get(testName).getRatio();
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.INSTANCE.toString(this) +
                System.lineSeparator() + MemoryConsumptionStatus.geInitMessage();
    }
}
