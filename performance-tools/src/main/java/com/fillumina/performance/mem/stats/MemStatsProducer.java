package com.fillumina.performance.mem.stats;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.mem.sample.AbstractMemSample;
import com.fillumina.performance.mem.sample.AllocatedMemSample;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.MemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSample;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.stats.AbstractStatsProducer;
import com.fillumina.performance.stats.StatsCreator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */

public class MemStatsProducer<S extends MemStats,
                              A extends AbstractMemSample<A,S>>
        extends AbstractStatsProducer<MemStatsProducer<S,A>, S> {

    // using MostUsedFilter this number is better being odd
    public static final int DEFAULT_SAMPLES = 33;
    private static final ListFilter<Double> DEFAULT_FILTER =
            MostUsedFilter.instance();

    private final MemSampleProducer<?,A> sampleProducer;
    private final int samples;
    private final ListFilter<Double> filter;
    private List<MemProgressionStatusListener> listeners;

    public static MemStatsProducer<AllocatedMemStats, AllocatedMemSample>
            createAllocated() {
        return new MemStatsProducer<>(new AllocatedMemSampleProducer());
    }

    public static MemStatsProducer<UsedMemStats, UsedMemSample>
            createUsed() {
        return new MemStatsProducer<>(new UsedMemSampleProducer());
    }

    public MemStatsProducer(MemSampleProducer<?,A> sampleProducer) {
        this(sampleProducer, DEFAULT_SAMPLES);
    }

    public MemStatsProducer(MemSampleProducer<?,A> sampleProducer, int samples) {
        this(sampleProducer, samples, DEFAULT_FILTER);
    }

    public MemStatsProducer(
            MemSampleProducer<?,A> sampleProducer,
            int samples,
            ListFilter<Double> filter) {
        this.sampleProducer = sampleProducer;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public MixedAssertableHolder get() {
        sampleProducer.clearAndAddAll(this);

        StatsCreator<S,A> sampleCollector = new StatsCreator<>(getName());
        setUpTests();
        for (int i=0; i<samples; i++) {
            Map<Class<?>, A> sample = sampleProducer.get();
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

    public AssertableHolder<S> memoryUsage(Runnable runnable) {
        clearTests();
        addTest(runnable);
        MixedAssertableHolder mixedHolder = get();
        return mixedHolder.getStats();
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

    protected void notifyListeners(Map<Class<?>,A> sampleMap,
            int currentSampleIndex, int totalSamples) {
        for (A sample : sampleMap.values()) {
            for (SampleValue v : sample.getValuesMap().values()) {
                notifyListeners(v.getName(), 0, 0, (long) v.getValue());
            }
        }
    }

    protected void notifyListeners(TName testName,
            int currentSampleIndex, int totalSamples, long memoryUsed) {
        if (listeners != null) {
            for (MemProgressionStatusListener l : listeners) {
                l.accepts(testName, currentSampleIndex, totalSamples, memoryUsed);
            }
        }
    }
}
