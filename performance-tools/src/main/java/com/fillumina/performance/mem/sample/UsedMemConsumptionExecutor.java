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

    private static final MemAnalyzer DEFAULT_MEM_ANALYZER =
            new MemAnalyzer(INSTANCE);

    public static MemAnalyzer createMemAnalyzer(int samples) {
        return new MemAnalyzer(INSTANCE, samples);
    }

    public static MemAnalyzer createMemAnalyzer() {
        return DEFAULT_MEM_ANALYZER;
    }

    private UsedMemConsumptionExecutor() {}

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        return execute(REPETITIONS, testable);
    }

    public long execute(int repetitions, Testable testable) {
        int i;
        testable.onBeforeSample(repetitions);
        MC.start();
        for (i = 0; i < repetitions; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return approxToMinMemory(MC.getUsedMemory() / repetitions);
    }

    //TODO use alignment from MC
    private long approxToMinMemory(long mem) {
        return (long) Math.floor(mem / 8.0) * 8;
    }
}
