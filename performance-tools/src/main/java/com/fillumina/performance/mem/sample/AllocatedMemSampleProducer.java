package com.fillumina.performance.mem.sample;

import com.fillumina.performance.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.infrastructure.test.LfsrRunnable;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.mem.AllocatedMemStats;
import com.fillumina.performance.util.MostUsedValueBag;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSampleProducer
        extends AbstractMemSampleProducer<AllocatedMemSample, AllocatedMemStats> {
    private static final int SAMPLES = 33;

    public static final AllocatedMemSampleProducer INSTANCE =
            new AllocatedMemSampleProducer();

    private final int zero;

    @Override
    protected Class<AllocatedMemSample> getSampleClass() {
        return AllocatedMemSample.class;
    }

    @Override
    protected AllocatedMemSample createSample(TNameMap<SampleValue> map) {
        return new AllocatedMemSample(map);
    }

    protected AllocatedMemSampleProducer() {
        MostUsedValueBag<Integer> bag = new MostUsedValueBag<>();
        for (int k=0; k<SAMPLES; k++) {
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
