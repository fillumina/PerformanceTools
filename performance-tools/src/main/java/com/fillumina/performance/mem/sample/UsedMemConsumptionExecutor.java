package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.speed.sample.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {

    public static final UsedMemConsumptionExecutor INSTANCE =
            new UsedMemConsumptionExecutor();

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(INSTANCE, samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return new MemAnalyzer(INSTANCE);
    }

    protected UsedMemConsumptionExecutor() {}

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        int i;
        testable.onBeforeSample(REPETITIONS);
        MC.start();
        for (i = 0; i < REPETITIONS; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return MC.getUsedMemory() / REPETITIONS;
    }
}
