package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.executor.test.LfsrRunnable;
import static com.fillumina.performance.mem.sample.AbstractMemSampleProducer.MC;
import com.fillumina.performance.util.collection.MostUsedValueBag;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleExecutor implements MemSampleExecutor {
    private final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory() / MC.getAlignment());

    /**
     * How many times a sample is retried when a garbage collection invalidates it
     * before the measurement is given up on.
     */
    private static final int MAX_ATTEMPTS = 10;

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
        for (int attempt = 1; ; attempt++) {
            AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, repetitions);
            executeGc();
            MemoryConsumption.INSTANCE.start();
            for (int i = 0; i < repetitions; i++) {
                runnable.run();
            }
            executeGc();
            long usedMemory = MemoryConsumption.INSTANCE.getUsedMemory();
            if (usedMemory != MemoryConsumption.GC_OCCURRED) {
                AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
                return usedMemory / repetitions;
            }
            AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
            if (attempt == MAX_ATTEMPTS) {
                throw new AssertionError("a garbage collection invalidated "
                        + MAX_ATTEMPTS + " consecutive memory samples");
            }
        }
    }

    private static void executeGc() {
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }
}
