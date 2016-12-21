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
        for (int k=0; k<30; k++) {
            bag.add((int)innerExecute(Testable.NULL));
        }
        zero = bag.getMostUsedValue();
        //System.out.println("Allocated zero = " + zero);
    }

    /**
     * @return how much memory {@link Testable} has allocated.
     */
    @Override
    public long execute(Testable testable) {
        return innerExecute(testable) - zero;
    }

    private long innerExecute(Testable testable) {
        int i;
        testable.onBeforeSample(REPETITIONS);
        executeGc();
        MC.start();
        for (i = 0; i < REPETITIONS; i++) {
            if (testable.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        executeGc();
        return MC.getUsedMemory() / REPETITIONS;
    }

    private static void executeGc() {
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
    }
}
