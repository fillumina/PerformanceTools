package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    public static final UsedMemConsumptionExecutor INSTANCE =
            new UsedMemConsumptionExecutor();
    protected static final MemoryConsumption MC = MemoryConsumption.INSTANCE;
    public static final MemAnalyzer MEM_ANALYZER =
            new MemAnalyzer(INSTANCE);

    private UsedMemConsumptionExecutor() {}

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        int i;
        int rep = MC.getByteGranularity();
        MC.start();
        for (i = 0; i < rep; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return nextPair(MC.getUsedMemory() / rep);
    }
}
