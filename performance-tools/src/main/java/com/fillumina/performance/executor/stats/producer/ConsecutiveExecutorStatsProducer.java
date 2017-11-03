package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.NamedTestExecutor;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 * Executes tests sequentially and returns them as an aggregate statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducer
        extends AbstractStatsProducerInstrumenter
                    <ConsecutiveExecutorStatsProducer,Stats<?>> {
    private final boolean consecutiveExecution;

    public interface Configuration {
        boolean isConsecutiveExecution();
    }

    public ConsecutiveExecutorStatsProducer(Configuration config) {
        this(config.isConsecutiveExecution());
    }

    public ConsecutiveExecutorStatsProducer(boolean consecutiveExecution) {
        this.consecutiveExecution = consecutiveExecution;
    }

    @Override
    public MixedAssertableHolder get() {
        if (!consecutiveExecution) {
            return executeProducer();
        }

        NamedTestExecutor<?,?,Runnable,MixedAssertableHolder> producer =
                getProducer();

        Stats<?> stats = executeConsecutively(producer);
        producer.clearTests();

        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        builder.addAssertable(stats.getClass(), getName(), stats);
        return builder.build();
    }

    private <V extends SingleStats> Stats<V> executeConsecutively(
            NamedTestExecutor<?, ?, Runnable, MixedAssertableHolder> producer) {
        Stats<V> joinStats = null;
        for (Map.Entry<TName, Runnable> entry : getTests().entrySet()) {
            final TName name = entry.getKey();
            final Runnable test = entry.getValue();

            producer.clearTests();
            producer.setName(name);
            producer.addTest(name, test);

            MixedAssertableHolder mixedHolder = producer.get();

            for (AssertableHolder<?> holder :
                    mixedHolder.getStatsMap().values()) {
                @SuppressWarnings("unchecked")
                Stats<V> stats = (Stats<V>) holder.getAssertable();
                if (joinStats == null) {
                    joinStats = stats;
                } else {
                    joinStats = joinStats.join(stats);
                }
            }
        }
        return joinStats;
    }
}
