package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.stats.Measure;
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

    // using MostUsedFilter this number is better being unpair
    public static final int DEFAULT_SAMPLES = 33;
    private static final MostUsedFilter<Long> DEFAULT_FILTER =
            new MostUsedFilter<Long>();

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
        this(executor, samples, DEFAULT_FILTER);
    }

    public MemAnalyzer(MemConsumptionExecutor executor,
            int samples, ListFilter<Long, Double> filter) {
        this.executor = executor;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public PHolder<MemStats> execute() {
        MemStatsBuilder msBuilder = new MemStatsBuilder(getTests().size());
        for (Map.Entry<String, Testable> entry : getTests().entrySet()) {
            final String testName = entry.getKey();
            final Testable testable = entry.getValue();
            testable.setUp();
            Measure m = memoryUsage(testName, testable);
            msBuilder.add(testName, m);
        }
        final MemStats memStats = msBuilder.build();
        PHolder<MemStats> perf =
                new PHolder<>(getName(), memStats);
        dispatchToConsumers(perf);
        return perf;
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

    public MemMeasure memoryUsage(Testable testable) {
        return memoryUsage("test", testable);
    }

    public MemMeasure memoryUsage(String testName,
            Testable testable) {
        List<Long> zeroList = new ArrayList<>(samples);
        List<Long> resultList = new ArrayList<>(samples);
        testable.onBeforeSample(samples);
        ComposedName fullName = getName().append(testName);
        for (int i=0; i<samples; i++) {
            final long zero = executor.execute("zero", Testable.NO_MEM);
            final long bytes = executor.execute(testName, testable) - zero;
            zeroList.add(zero);
            resultList.add(bytes);
            notifyStatusListeners(fullName, i, samples, testName, bytes);
        }

        final List<Long> filteredList = filter.filter(resultList, LONG_EXTRACTOR);

        MemMeasure measure = new MemMeasure(filteredList);
        measure.log(MemoryAllocatorInfo.INSTANCE.getDebugString());
        measure.log("samples  = " + samples);
        measure.log("zeroes   = " + zeroList.toString());
        measure.log("values   = " + resultList.toString());
        measure.log("filter   = " + filter.toString());
        measure.log("filtered = " + filteredList.toString());
        measure.log("measure  = " + measure.toString());
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

    private void notifyStatusListeners(
            ComposedName fullTestName,
            int sample,
            int totalSamples,
            String testName,
            long memoryUsed) {
        if (statusListeners != null) {
            for (MemProgressionStatusListener l : statusListeners) {
                l.accepts(fullTestName, sample, totalSamples, testName, memoryUsed);
            }
        }
    }
}
