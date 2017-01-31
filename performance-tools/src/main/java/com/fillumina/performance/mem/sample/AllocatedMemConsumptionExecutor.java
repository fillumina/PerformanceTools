package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.MostUsedValueBag;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemConsumptionExecutor
        extends AbstractMemConsumtionExecutor {
    private static final int SAMPLES = 33;

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
            bag.add((int)innerExecute(2, Testable.FASTEST));
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
        testable.onBeforeSample(repetitions);
        executeGc();
        MC.start();
        for (i = 0; i < repetitions; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        executeGc();
        return MC.getUsedMemory() / repetitions;
    }

    private static void executeGc() {
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }
}
