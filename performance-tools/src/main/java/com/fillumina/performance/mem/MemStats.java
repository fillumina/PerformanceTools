package com.fillumina.performance.mem;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.TName;
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

    private final Map<TName, MemPerformance> map;

    public MemStats(Map<TName, MemPerformance> map) {
        this.map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    public Map<TName, MemPerformance> getPerformances() {
        return map;
    }

    @Override
    public Collection<TName> getTestNames() {
        return map.keySet();
    }

    @Override
    public Measure getMeasure(TName testName) {
        final MemPerformance performance = map.get(testName);
        if (performance == null) {
            throw new TestNotFoundException(testName, map.keySet());
        }
        return performance.getUsedMemory();
    }

    @Override
    public MeasureRatio getRatioWithGreaterTest(TName testName,
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
