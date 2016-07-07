package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    private static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(new AllocatedMemConsumptionExecutor(), samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return new MemAnalyzer(new AllocatedMemConsumptionExecutor());
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        int i;
        int rep = MC.getByteGranularity();
        testable.onBeforeSample(rep);
        executeGc();
        MC.start();
        for (i = 0; i < rep; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        executeGc();
        return nextPair(MC.getUsedMemory() / rep);
    }

    private static void executeGc() {
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }
}
