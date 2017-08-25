package com.fillumina.performance.mem;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.infrastructure.AbstractAssertableProducer;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemoryAllocatorInfo;
import com.fillumina.performance.util.tname.TName;
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
public class MemStatsProducer
        extends AbstractAssertableProducer<MemStatsProducer, Runnable>
        implements StatsProducer {

    // using MostUsedFilter this number is better being unpair
    public static final int DEFAULT_SAMPLES = 33;
    private static final MostUsedFilter<Long> DEFAULT_FILTER =
            new MostUsedFilter<Long>();

    private static final ValueExtractor<Long, Double> LONG_EXTRACTOR =
            (Long t) -> (double)t;

    private final MemConsumptionExecutor executor;
    private final int samples;
    private final ListFilter<Long, Double> filter;
    private List<MemProgressionStatusListener> statusListeners;

    public MemStatsProducer(MemConsumptionExecutor executor) {
        this(executor, DEFAULT_SAMPLES);
    }

    public MemStatsProducer(MemConsumptionExecutor executor, int samples) {
        this(executor, samples, DEFAULT_FILTER);
    }

    public MemStatsProducer(MemConsumptionExecutor executor,
            int samples,
            ListFilter<Long, Double> filter) {
        this.executor = executor;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public MixedAssertableHolder execute() {
        MemStatsBuilder msBuilder =
                new MemStatsBuilder(executor, getTests().size());
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            final TName testName = entry.getKey();
            final Runnable testable = entry.getValue();
            AnnotatedRunnableSetter.INSTANCE.setUp(testable);
            Measure m = memoryUsage(testName, testable);
            AnnotatedRunnableSetter.INSTANCE.tearDown(testable);
            msBuilder.add(testName, m);
        }
        final MemStats memStats = msBuilder.build();
        dispatchToConsumers(memStats);
        return MixedAssertableHolder.builder()
                .addAssertable(memStats.getClass(), getName(), memStats)
                .build();
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

    public MemMeasure memoryUsage(Runnable runnable) {
        return memoryUsage(TN.tname("test"), runnable);
    }

    public MemMeasure memoryUsage(TName testName,
            Runnable runnable) {
        List<Long> zeroList = new ArrayList<>(samples);
        List<Long> resultList = new ArrayList<>(samples);
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, samples);

        for (int i=0; i<samples; i++) {
            long zero = executor.execute(TN.tname("zero"), new LfsrRunnable());
            long bytes = executor.execute(testName, runnable) - zero;
            zeroList.add(zero);
            resultList.add(bytes);
            notifyStatusListeners(testName, i, samples, bytes);
        }
        AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, samples);
        List<Long> filteredList = filter.filter(resultList, LONG_EXTRACTOR);

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
    public <T extends Instrumenter<StatsProducer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    public MemStatsProducer addMemProgressionStatusListener(
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
            TName testName,
            int sample,
            int totalSamples,
            long memoryUsed) {
        if (statusListeners != null) {
            for (MemProgressionStatusListener l : statusListeners) {
                l.accepts(testName, sample, totalSamples, memoryUsed);
            }
        }
    }
}
