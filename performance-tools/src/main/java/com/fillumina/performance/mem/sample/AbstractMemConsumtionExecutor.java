package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceConsumerNotifier;
import com.fillumina.performance.infrastructure.TName;
import com.fillumina.performance.infrastructure.PHolder;

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

    public abstract long execute(Runnable runnable);

    @Override
    public long execute(String testName, Runnable runnable) {
        long bytes = execute(runnable);
        final PHolder<MemSample> performanceHolder =
                new PHolder<>(
                        TName.EMPTY.append(testName),
                        new MemSample(testName, bytes));
        dispatchToConsumers(performanceHolder);
        return bytes;
    }
}
