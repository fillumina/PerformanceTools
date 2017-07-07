package com.fillumina.performance.mem.sample;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import com.fillumina.performance.mem.AllocatedMemStats;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemPerformance;
import com.fillumina.performance.util.MostUsedValueBag;
import com.fillumina.performance.util.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {
    private static final int SAMPLES = 33;

    public static final AllocatedMemConsumptionExecutor INSTANCE =
            new AllocatedMemConsumptionExecutor();

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(INSTANCE, samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return new MemAnalyzer(INSTANCE);
    }

    private final int zero;

    protected AllocatedMemConsumptionExecutor() {
        MostUsedValueBag<Integer> bag = new MostUsedValueBag<>();
        for (int k=0; k<SAMPLES; k++) {
            bag.add((int)innerExecute(2, new LfsrRunnable()));
        }
        //System.out.println("ZERO = " + bag.toString());
        zero = bag.getMostUsedValue();
    }

    @Override
    public AllocatedMemStats createStats(Map<TName, MemPerformance> map) {
        return new AllocatedMemStats(map);
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return execute(REPETITIONS, runnable);
    }

    public long execute(int repetitions, Runnable runnable) {
        return innerExecute(repetitions, runnable) - zero;
    }

    private long innerExecute(int repetitions, Runnable runnable) {
        int i;
        long usedMemory;
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, repetitions);
        executeGc();
        MC.start();
        for (i = 0; i < repetitions; i++) {
            runnable.run();
        }
        executeGc();
        usedMemory = MC.getUsedMemory() / repetitions;
        AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
        return usedMemory;
    }

    private static void executeGc() {
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }
}
