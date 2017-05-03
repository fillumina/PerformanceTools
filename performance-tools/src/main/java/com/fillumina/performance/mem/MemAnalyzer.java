package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.infrastructure.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.util.TName;
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
        extends AbstractPerformanceProducer<MemAnalyzer, MemStats, Runnable>
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
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            final TName testName = entry.getKey();
            final Runnable testable = entry.getValue();
            AnnotatedRunnableSetter.INSTANCE.setUp(testable);
            Measure m = memoryUsage(testName, testable);
            AnnotatedRunnableSetter.INSTANCE.tearDown(testable);
            msBuilder.add(testName, m);
        }
        final MemStats memStats = msBuilder.build();
        dispatchToConsumers(getName(), memStats);
        PHolder<MemStats> perf =
                new PHolder<>(getName(), memStats);
        return perf;
    }

    public Map<TName, Measure> memoryUsage(
            final Map<TName, Runnable> tests) {
        Map<TName, Measure> measures = new LinkedHashMap<>(tests.size());
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            TName name = entry.getKey();
            Runnable test = entry.getValue();
            measures.put(name, memoryUsage(name, test));
        }
        return measures;
    }

    public MemMeasure memoryUsage(Runnable testable) {
        return memoryUsage(TN.n("test"), testable);
    }

    public MemMeasure memoryUsage(TName testName,
            Runnable runnable) {
        List<Long> zeroList = new ArrayList<>(samples);
        List<Long> resultList = new ArrayList<>(samples);
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, samples);

        TName fullName = getName().append(testName);
        for (int i=0; i<samples; i++) {
            final long zero = executor.execute(TN.n("zero"), new LfsrTestable());
            final long bytes = executor.execute(testName, runnable) - zero;
            zeroList.add(zero);
            resultList.add(bytes);
            notifyStatusListeners(fullName, i, samples, testName, bytes);
        }
        AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, samples);
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
            TName fullTestName,
            int sample,
            int totalSamples,
            TName testName,
            long memoryUsed) {
        if (statusListeners != null) {
            for (MemProgressionStatusListener l : statusListeners) {
                l.accepts(fullTestName, sample, totalSamples, testName, memoryUsed);
            }
        }
    }
}
