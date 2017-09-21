package com.fillumina.performance.mem.sample;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import static com.fillumina.performance.mem.sample.AbstractMemSampleProducer.MC;
import com.fillumina.performance.test.LfsrRunnable;
import com.fillumina.performance.util.MostUsedValueBag;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleExecutor implements MemSampleExecutor {
    private final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    private final int zero;

    public static final AllocatedMemSampleExecutor INSTANCE =
            new AllocatedMemSampleExecutor();

    private AllocatedMemSampleExecutor() {
        MostUsedValueBag<Integer> bag = new MostUsedValueBag<>();
        for (int k=0; k<33; k++) {
            bag.add((int)innerExecute(2, new LfsrRunnable()));
        }
        //System.out.println("ZERO = " + bag.toString());
        zero = bag.getMostUsedValue();
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return execute(REPETITIONS, runnable);
    }

    @Override
    public long execute(int repetitions, Runnable runnable) {
        return innerExecute(repetitions, runnable) - zero;
    }

    public static long innerExecute(int repetitions, Runnable runnable) {
        int i;
        long usedMemory;
        AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, repetitions);
        executeGc();
        MemoryConsumption.INSTANCE.start();
        for (i = 0; i < repetitions; i++) {
            runnable.run();
        }
        executeGc();
        usedMemory = MemoryConsumption.INSTANCE.getUsedMemory() / repetitions;
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
