package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    private static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(new UsedMemConsumptionExecutor(), samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return new MemAnalyzer(new UsedMemConsumptionExecutor());
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        int i;
        int rep = MC.getByteGranularity();
        testable.onBeforeSample(rep);
        MC.start();
        for (i = 0; i < rep; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return nextPair(MC.getUsedMemory() / rep);
    }
}
