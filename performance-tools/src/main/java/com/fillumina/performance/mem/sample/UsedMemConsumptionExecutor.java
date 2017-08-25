package com.fillumina.performance.mem.sample;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.mem.MemStatsProducer;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor<UsedMemSample> {

    public static final UsedMemConsumptionExecutor INSTANCE =
            new UsedMemConsumptionExecutor();

    public static MemStatsProducer createMemAnalyzer(int samples) {
        return new MemStatsProducer(INSTANCE, samples);
    }

    public static MemStatsProducer createMemAnalyzer() {
        return new MemStatsProducer(INSTANCE);
    }

    private UsedMemConsumptionExecutor() {}

    @Override
    protected Class<UsedMemSample> getSampleClass() {
        return UsedMemSample.class;
    }

    @Override
    protected UsedMemSample createSample(TNameMap<TestSample> map) {
        return new UsedMemSample(map);
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return execute(REPETITIONS, runnable);
    }

    public long execute(int repetitions, Runnable runnable) {
        int i;
        long usedMemory;
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, repetitions);
        MC.start();
        for (i = 0; i < repetitions; i++) {
            runnable.run();
        }
        usedMemory = approxToMinMemory(MC.getUsedMemory() / repetitions);
        AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
        return usedMemory;
    }

    private long approxToMinMemory(long mem) {
        final long alignment = MC.getAlignment();
        return (long) Math.floor(mem * 1.0 / alignment) * alignment;
    }
}
