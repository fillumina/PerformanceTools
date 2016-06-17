package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryAnalyzer {
    public static final int MEMORY_GRANULARITY =
            MemoryConsumption.ASSESSMENT.minGranularityByte;
    private final ListFilter<Long, Double> filter;
    private final static ValueExtractor<Long, Double> LONG_EXTRACTOR =
            new ValueExtractor<Long,Double>() {
                @Override
                public Double getValue(Long t) {
                    return (double)t;
                }
            };

    public static final MemoryAnalyzer INSTANCE = new MemoryAnalyzer();

    public MemoryAnalyzer() {
        this(3.0);
    }

    public MemoryAnalyzer(double stdFactor) {
        this.filter = new OutlierEliminatorFilter<>(stdFactor);
    }

    public Map<String, Measure> memoryUsage(final Map<String, Testable> tests,
            final int samples) {
        Map<String,Measure> measures = new LinkedHashMap<>(tests.size());
        for (Map.Entry<String,Testable> entry : tests.entrySet()) {
            String name = entry.getKey();
            Testable test = entry.getValue();

            measures.put(name, memoryUsage(samples, test));
        }
        return measures;
    }

    public Measure memoryUsage(int samples, Testable testable) {
        List<Long> list = new ArrayList<>(samples);
        for (int i=0; i<samples; i++) {
            list.add(execute(testable));
        }
        return new OnlineMeasure(filter.filter(list, LONG_EXTRACTOR));
    }

    /**
     * @return how much memory {@link Testable} is using (16 byte granularity).
     */
    public long execute(Testable testable) {
        MemoryConsumption mem = new MemoryConsumption();
        mem.start();
        if (testable.test() == this) {
            throw new AssertionError("cannot happen");
        }
        return mem.getUsedMemory();
    }
}
