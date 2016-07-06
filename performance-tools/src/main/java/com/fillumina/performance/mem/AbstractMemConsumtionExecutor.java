package com.fillumina.performance.mem;

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

    public abstract long execute(Testable testable);

    @Override
    public long execute(String testName, Testable testable) {
        long bytes = execute(testable);
        dispatchToConsumers(ComposedName.create(testName),
                new MemSample(testName, bytes));
        return bytes;
    }

    protected static long nextPair(long x) {
        return x + (x & 1);
    }
}
