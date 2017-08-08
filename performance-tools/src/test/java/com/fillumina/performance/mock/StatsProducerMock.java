package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractAssertableProducer;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerMock
        extends AbstractAssertableProducer<StatsProducerMock,Runnable>
        implements StatsProducer,
                   Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedTree<TName, Runnable> executedTests =
            new LinkedTree<>();

    public LinkedTree<TName, Runnable> getExecutedTests() {
        return executedTests;
    }

    @Override
    public <T extends Instrumenter<StatsProducer>> T instrumentedBy(
            T instrumenter) {
        return null;
    }

    @Override
    public MixedAssertableHolder execute() {
        final Map<TName, Runnable> tests = getTests();
        LinkedTree<TName,Runnable> subTree =
                executedTests.addTree(getName(), null);
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            TName fullName = getName().append(entry.getKey());
            subTree.put(fullName, entry.getValue());
        }
        return MixedAssertableHolder.builder()
                .addAssertable(getStatsType(), getName(), createStats())
                .build();
    }

    protected Class<? extends Assertable> getStatsType() {
        return AverageTimeStats.class;
    }

    protected Assertable createStats() {
        return null;
    }
}
