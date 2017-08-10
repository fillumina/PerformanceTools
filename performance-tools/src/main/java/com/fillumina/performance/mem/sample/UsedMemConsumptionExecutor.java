package com.fillumina.performance.mem.sample;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.SingleMemStats;
import com.fillumina.performance.mem.UsedMemStats;
import com.fillumina.performance.util.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    public static final UsedMemConsumptionExecutor INSTANCE =
            new UsedMemConsumptionExecutor();

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(INSTANCE, samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return new MemAnalyzer(INSTANCE);
    }

    private UsedMemConsumptionExecutor() {}

    @Override
    public UsedMemStats createStats(Map<TName, SingleMemStats> map) {
        return new UsedMemStats(map);
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
