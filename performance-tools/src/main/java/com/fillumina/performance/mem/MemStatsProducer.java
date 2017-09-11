package com.fillumina.performance.mem;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.stats.AbstractStatsProducer;
import com.fillumina.performance.infrastructure.stats.StatsCreator;
import com.fillumina.performance.mem.sample.AbstractMemSample;
import com.fillumina.performance.mem.sample.AllocatedMemSample;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.MemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSample;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.instrument.Instrumenter;

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

    private final MemSampleProducer<?,A> executor;
    private final int samples;
    private final ListFilter<Double> filter;

    public static MemStatsProducer<AllocatedMemStats, AllocatedMemSample>
            createAllocatedMemSampleProducer() {
        return new MemStatsProducer<>(AllocatedMemSampleProducer.INSTANCE);
    }

    public static MemStatsProducer<UsedMemStats, UsedMemSample>
            createUsedMemSampleProducer() {
        return new MemStatsProducer<>(UsedMemSampleProducer.INSTANCE);
    }

    public MemStatsProducer(MemSampleProducer<?,A> executor) {
        this(executor, DEFAULT_SAMPLES);
    }

    public MemStatsProducer(MemSampleProducer<?,A> executor, int samples) {
        this(executor, samples, DEFAULT_FILTER);
    }

    public MemStatsProducer(
            MemSampleProducer<?,A> executor,
            int samples,
            ListFilter<Double> filter) {
        this.executor = executor;
        this.samples = samples;
        this.filter = filter;
    }

    @Override
    public MixedAssertableHolder get() {
        executor.clearAndAddAll(this);

        StatsCreator<S,A> sampleCollector = new StatsCreator<>(getName());
        setUpTests();
        for (int i=0; i<samples; i++) {
            sampleCollector.addSample(executor.get());
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

    @Override
    public <T extends Instrumenter<MemStatsProducer<S,A>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
