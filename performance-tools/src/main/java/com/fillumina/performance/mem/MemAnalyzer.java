package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.ConcreteOnlineMeasure;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAnalyzer
        extends AbstractPerformanceProducer<MemAnalyzer, MemStats, Testable>
        implements StatsProducer<MemStats> {
    public static final int MEMORY_GRANULARITY =
            MemoryConsumption.INSTANCE.getByteGranularity();

    private final static ValueExtractor<Long, Double> LONG_EXTRACTOR =
            new ValueExtractor<Long,Double>() {
                @Override
                public Double getValue(Long t) {
                    return (double)t;
                }
            };

    public static final MemAnalyzer INSTANCE = new MemAnalyzer();
    public static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    private final ListFilter<Long, Double> filter;
    private final int samples;

    public MemAnalyzer() {
        this(30, 3.0);
    }

    public MemAnalyzer(int samples, double stdFactor) {
        this.samples = samples;
        this.filter = new OutlierEliminatorFilter<>(stdFactor);
    }

    public Map<String, Measure> memoryUsage(final int samples,
            final Map<String, Testable> tests) {
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
        return new ConcreteOnlineMeasure(MemUnit.INSTANCE,
                filter.filter(list, LONG_EXTRACTOR));
    }

    /**
     * @return how much memory {@link Testable} is using (16 byte granularity).
     */
    public long execute(Testable testable) {
        //MemoryConsumption MC = new MemoryConsumption();
        int repeat = MemoryConsumption.INSTANCE.getByteGranularity() * 2;
        int i;
        MC.start();
        for (i = 0; i < repeat; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return MC.getUsedMemory() / repeat;
    }

    @Override
    public PerformanceHolder<MemStats> execute() {
        MemStatsBuilder builder = new MemStatsBuilder(getTests().size());
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            final String testName = entry.getKey();
            final Testable testable = entry.getValue();

            Measure m = memoryUsage(samples, testable);
            builder.add(testName, m);
        }
        return new PerformanceHolder<>(builder.build());
    }

    @Override
    public <T extends Instrumenter<StatsProducer<MemStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
