package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryConsumptionStatus;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
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
        extends AbstractPerformanceProducer<MemAnalyzer, MemStats, MemStats, Testable>
        implements StatsProducer<MemStats> {

    public static final int DEFAULT_SAMPLES = 10;

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
    private List<MemProgressionStatusListener> statusListeners;

    public MemAnalyzer(MemConsumptionExecutor executor) {
        this(executor, DEFAULT_SAMPLES);
    }

    public MemAnalyzer(MemConsumptionExecutor executor, int samples) {
        this(executor, samples,
//                new OutlierEliminatorFilter<Long>(DEFAULT_STANDARD_FACTOR));
                // TODO if there isn't at least 50% of same value, repeat test
                new MostUsedFilter<Long>());
    }

    public MemAnalyzer(MemConsumptionExecutor executor,
            int samples, ListFilter<Long, Double> filter) {
        this.executor = executor;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public TreeHolder<MemStats, MemStats> execute() {
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
        return new TreeHolder<>(memStats);
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
            final long bytes = executor.execute(testName, testable);
            list.add(bytes);
            notifyStatusListeners(i, samples, testName, bytes);
        }
        DimensionalOnlineMeasure measure =
                new DimensionalOnlineMeasure(MemUnit.INSTANCE,
                        filter.filter(list, LONG_EXTRACTOR));

        System.out.println(MemoryConsumptionStatus.geInitMessage());
        System.out.println("values=" + list.toString());
        System.out.println("measure=" + measure.toString());

        return measure;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<MemStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    public MemAnalyzer addMemProgressionStatusListener(
            MemProgressionStatusListener listener) {
        if (listener != null) {
            if (statusListeners == null) {
                statusListeners = new ArrayList<>();
            }
            statusListeners.add(listener);
        }
        return this;
    }

    private void notifyStatusListeners(int sample,
            int totalSamples,
            String testName,
            long memoryUsed) {
        if (statusListeners != null) {
            for (MemProgressionStatusListener l : statusListeners) {
                l.accepts(sample, totalSamples, testName, memoryUsed);
            }
        }
    }
}
