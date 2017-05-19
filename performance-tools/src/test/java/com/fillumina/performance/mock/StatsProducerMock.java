package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerMock<A extends Assertable>
        extends AbstractPerformanceProducer<StatsProducerMock<A>,
                                            A,
                                            Runnable>
        implements StatsProducer<A>,
                   Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedTree<TName, Runnable> executedTests =
            new LinkedTree<>();

    public LinkedTree<TName, Runnable> getExecutedTests() {
        return executedTests;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<A>>> T instrumentedBy(
            T instrumenter) {
        return null;
    }

    @Override
    public PHolder<A> execute() {
        final Map<TName, Runnable> tests = getTests();
        LinkedTree<TName,Runnable> subTree =
                executedTests.addTree(getName(), null);
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            TName fullName = getName().append(entry.getKey());
            subTree.put(fullName, entry.getValue());
        }
        return new PHolder<>(getName(), createStats());
    }

    protected A createStats() {
        return null;
    }
}
