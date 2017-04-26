package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MockStatsProducer<A extends Assertable>
        extends AbstractPerformanceProducer<MockStatsProducer<A>,
                                            A,
                                            Runnable>
        implements StatsProducer<A>,
                   Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedTree<String, Runnable> executedTests =
            new LinkedTree<>();

    public LinkedTree<String, Runnable> getExecutedTests() {
        return executedTests;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<A>>> T instrumentedBy(
            T instrumenter) {
        return null;
    }

    @Override
    public PHolder<A> execute() {
        final LinkedHashMap<String, Runnable> tests = getTests();
        executedTests
                .addTree(getName().toStringWithSeparator("_"), null)
                .putAll(tests);
        return new PHolder<>(getName(), createStats());
    }

    protected A createStats() {
        return null;
    }
}
