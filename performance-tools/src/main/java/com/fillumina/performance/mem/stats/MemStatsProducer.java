package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.MixedStatsHolderCreator;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.MemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemStatsProducer
        extends AbstractStatsProducer<MemStatsProducer> {

    // using MostUsedFilter this number is better being odd
    public static final int DEFAULT_SAMPLES = 33;
    private static final ListFilter<Double> DEFAULT_FILTER =
            MostUsedFilter.instance();

    private final MemSampleProducer<?> sampleProducer;
    private final int samples;
    private final ListFilter<Double> filter;
    private List<MemProgressionStatusListener> listeners;

    public static MemStatsProducer createAllocated() {
        return new MemStatsProducer(new AllocatedMemSampleProducer());
    }

    public static MemStatsProducer createUsed() {
        return new MemStatsProducer(new UsedMemSampleProducer());
    }

    public MemStatsProducer(MemSampleProducer<?> sampleProducer) {
        this(sampleProducer, DEFAULT_SAMPLES);
    }

    public MemStatsProducer(MemSampleProducer<?> sampleProducer, int samples) {
        this(sampleProducer, samples, DEFAULT_FILTER);
    }

    public MemStatsProducer(MemSampleProducer<?> sampleProducer,
            int samples,
            ListFilter<Double> filter) {
        this.sampleProducer = sampleProducer;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public MixedStatsHolder get() {
        sampleProducer.clearAndAddAllTests(this);

        MixedStatsHolderCreator sampleCollector = new MixedStatsHolderCreator(getPathName());
        setUpTests();
        for (int i=0; i<samples; i++) {
            Map<StatsType, Sample> sample = sampleProducer.get();
            notifyListeners(sample, i, samples);
            sampleCollector.addSample(sample);
        }
        tearDownTests();

        return sampleCollector.getMixedAssertableHolder(filter);
    }

    private void setUpTests() {
        getTests().values().forEach(
                r -> AnnotatedRunnableSetter.INSTANCE.setUp(r));
    }

    private void tearDownTests() {
        getTests().values().forEach(
                r -> AnnotatedRunnableSetter.INSTANCE.tearDown(r));
    }

    public StatsHolder memoryUsage(Runnable runnable) {
        clearTests();
        addTest(runnable);
        MixedStatsHolder mixedHolder = get();
        return mixedHolder.getFirstStatsHolder();
    }

    public void addMemProgressionStatusListener(
            MemProgressionStatusListener consoleMemProgressionListener) {
        if (consoleMemProgressionListener != null) {
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(consoleMemProgressionListener);
        }
    }

    protected void notifyListeners(Map<StatsType,Sample> sampleMap,
            int currentSampleIndex, int totalSamples) {
        for (Sample sample : sampleMap.values()) {
            for (SampleValue v : sample.getValuesMap().values()) {
                notifyListeners(v.getPathName(), 0, 0,
                        (long) v.getQuantity().as(MemUnit.B));
            }
        }
    }

    protected void notifyListeners(PathName testName,
            int currentSampleIndex, int totalSamples, long memoryUsed) {
        if (listeners != null) {
            for (MemProgressionStatusListener l : listeners) {
                l.accepts(testName, currentSampleIndex, totalSamples, memoryUsed);
            }
        }
    }
}
