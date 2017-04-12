package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStats
        extends AbstractAssertable
        implements Assertable, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, MemPerformance> map;

    public MemStats(Map<String, MemPerformance> map) {
        this.map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    public Map<String, MemPerformance> getPerformances() {
        return map;
    }

    @Override
    public Collection<String> getTestNames() {
        return map.keySet();
    }

    @Override
    public Measure getMeasure(String testName) {
        final MemPerformance performance = map.get(testName);
        if (performance == null) {
            throw new IllegalStateException("test '" + testName + "' not found");
        }
        return performance.getUsedMemory();
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName,
            Ratio confidence) {
        return map.get(testName).getRatio();
    }

    @Override
    public String toString() {
        return MemStatsTableStringGenerator.INSTANCE.toString(this) +
                System.lineSeparator() +
                MemoryAllocatorInfo.INSTANCE.getDebugString();
    }
}
