package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AbstractAssertableConsumerNotifier;
import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemConsumtionExecutor
        extends AbstractAssertableConsumerNotifier
                        <AbstractMemConsumtionExecutor>
        implements MemConsumptionExecutor {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    protected final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    public abstract long execute(Runnable runnable);

    @Override
    public long execute(TName testName, Runnable runnable) {
        long bytes = execute(runnable);
        final MemSample memSample = new MemSample(testName, bytes);
        dispatchToConsumers(memSample);
        return bytes;
    }
}
