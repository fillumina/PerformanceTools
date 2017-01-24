package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceConsumerNotifier;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemConsumtionExecutor
        extends AbstractPerformanceConsumerNotifier
            <AbstractMemConsumtionExecutor, MemSample>
        implements MemConsumptionExecutor {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;
    protected final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    public abstract long execute(Testable testable);

    @Override
    public long execute(String testName, Testable testable) {
        long bytes = execute(testable);
        dispatchToConsumers(
                ComposedName.create(testName),
                new MemSample(testName, bytes));
        return bytes;
    }
}
