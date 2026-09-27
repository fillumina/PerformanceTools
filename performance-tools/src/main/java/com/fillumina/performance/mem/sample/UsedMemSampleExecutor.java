package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import static com.fillumina.performance.mem.sample.AbstractMemSampleProducer.MC;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSampleExecutor implements MemSampleExecutor {
    /**
     * How many times a sample is retried when a garbage collection invalidates it
     * before the measurement is given up on.
     */
    private static final int MAX_ATTEMPTS = 10;

    private final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory() / MC.getAlignment());

    public static final UsedMemSampleExecutor INSTANCE =
            new UsedMemSampleExecutor();

    private UsedMemSampleExecutor() {}

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Runnable runnable) {
        return execute(REPETITIONS, runnable);
    }

    @Override
    public long execute(int repetitions, Runnable runnable) {
        // trying to rebase memory evaluator
        Runnable rn = new Runnable() {
            @Override
            public void run() {
                Object o = new int[0];
                if (o.hashCode() == 0) {
                    throw new RuntimeException("the horror!");
                }
            }
        };
        long baseline = innerExecute(REPETITIONS, rn);
        long mem = innerExecute(repetitions, runnable);
        //System.out.println("baseline=" + baseline +
        //    ", mem=" + mem + ", minMem=" + MC.getMinimalAllocableMemory());
        return mem - (baseline - MC.getMinimalAllocableMemory());
    }

    private long innerExecute(int repetitions, Runnable runnable) {
        for (int attempt = 1; ; attempt++) {
            AnnotatedRunnableSetter.INSTANCE.onBeforeSample(runnable, repetitions);
            MC.start();
            for (int i = 0; i < repetitions; i++) {
                runnable.run();
            }
            long usedMemory = MC.getUsedMemory();
            if (usedMemory != MemoryConsumption.GC_OCCURRED) {
                AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
                return approxToMinMemory(usedMemory / repetitions);
            }
            AnnotatedRunnableSetter.INSTANCE.onAfterSample(runnable, repetitions);
            if (attempt == MAX_ATTEMPTS) {
                throw new AssertionError("a garbage collection invalidated "
                        + MAX_ATTEMPTS + " consecutive memory samples");
            }
        }
    }

    private long approxToMinMemory(long mem) {
        final long alignment = MC.getAlignment();
        return (long) Math.floor(mem * 1.0 / alignment) * alignment;
    }

}
