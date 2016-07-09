package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import static com.fillumina.performance.util.filter.OutlierEliminatorFilter.DEFAULT_STANDARD_FACTOR;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
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

    private static final ValueExtractor<Long, Double> LONG_EXTRACTOR =
            new ValueExtractor<Long,Double>() {
                @Override
                public Double getValue(Long t) {
                    return (double)t;
                }
            };

    private final MemConsumptionExecutor executor;
    private final ListFilter<Long, Double> filter;
    private final int samples;

    public MemAnalyzer(MemConsumptionExecutor executor) {
        this(executor, 33, DEFAULT_STANDARD_FACTOR);
    }

    public MemAnalyzer(MemConsumptionExecutor executor, int samples) {
        this(executor, samples, DEFAULT_STANDARD_FACTOR);
    }

    public MemAnalyzer(MemConsumptionExecutor executor,
            int samples, double stdFactor) {
        this.executor = executor;
        this.samples = samples;
        this.filter = new OutlierEliminatorFilter<>(stdFactor);
    }

    @Override
    public PerformanceHolder<MemStats> execute() {
        MemStatsBuilder msBuilder = new MemStatsBuilder(getTests().size());
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            final String testName = entry.getKey();
            final Testable testable = entry.getValue();
            testable.setUp();
            Measure m = memoryUsage(testName, testable);
            msBuilder.add(testName, m);
        }
        final MemStats memStats = msBuilder.build();
        dispatchToConsumers(getName(), memStats);
        return new PerformanceHolder<>(memStats);
    }

    public Map<String, Measure> memoryUsage(
            final Map<String, Testable> tests) {
        Map<String, Measure> measures = new LinkedHashMap<>(tests.size());
        for (Map.Entry<String, Testable> entry : tests.entrySet()) {
            String name = entry.getKey();
            Testable test = entry.getValue();
            measures.put(name, memoryUsage(name, test));
        }
        return measures;
    }

    public Measure memoryUsage(String testName, Testable testable) {
        List<Long> list = new ArrayList<>(samples);
        testable.setUp();
        for (int i=0; i<samples; i++) {
            list.add(executor.execute(testName, testable));
        }
        return new DimensionalOnlineMeasure(MemUnit.INSTANCE,
                filter.filter(list, LONG_EXTRACTOR));
    }

    @Override
    public <T extends Instrumenter<StatsProducer<MemStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    static long nextPair(long x) {
        return x + (x & 1);
    }
}
