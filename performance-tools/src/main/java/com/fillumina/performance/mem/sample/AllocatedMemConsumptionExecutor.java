package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.util.MostUsedValueBag;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {
    private static final int SAMPLES = 33;

    /** Do nothing Test. Use as baseline. */
    // TODO this test will be evicted!! remove and rename it to NO_MEM
    private Testable NO_MEMORY = new Testable() {
        @Override public void setUp() {}
        @Override public void onBeforeSample(int iterations) {}
        @Override public void test() {}
        @Override public void onAfterSample(int iterations) {}
        @Override public void tearDown() {}
    };

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
            // TODO FASTEST is prone to eviction shouldn't use NO_MEM?
            bag.add((int)innerExecute(2, NO_MEMORY));
        }
        //System.out.println("ZERO = " + bag.toString());
        zero = bag.getMostUsedValue();
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        return execute(REPETITIONS, testable);
    }

    public long execute(int repetitions, Testable testable) {
        return innerExecute(repetitions, testable) - zero;
    }

    private long innerExecute(int repetitions, Testable testable) {
        int i;
        long usedMemory;
        testable.onBeforeSample(repetitions);
        executeGc();
        MC.start();
        for (i = 0; i < repetitions; i++) {
            testable.test();
        }
        executeGc();
        usedMemory = MC.getUsedMemory() / repetitions;
        testable.onAfterSample(repetitions);
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
