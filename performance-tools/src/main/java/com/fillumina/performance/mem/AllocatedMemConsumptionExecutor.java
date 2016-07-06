package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    public static final AllocatedMemConsumptionExecutor INSTANCE =
            new AllocatedMemConsumptionExecutor();
    protected static final MemoryConsumption MC = MemoryConsumption.INSTANCE;
    public static final MemAnalyzer MEM_ANALYZER =  new MemAnalyzer(INSTANCE);

    private AllocatedMemConsumptionExecutor() {}

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        int i;
        int rep = MC.getByteGranularity();
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
