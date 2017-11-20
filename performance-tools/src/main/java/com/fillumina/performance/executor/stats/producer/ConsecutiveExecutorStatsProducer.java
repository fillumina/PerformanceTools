package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
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
                    <ConsecutiveExecutorStatsProducer> {
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

        TestExecutor<?,?,Runnable,MixedAssertableHolder> producer =
                getProducer();

        Stats stats = executeConsecutively(producer);
        producer.clearTests();

        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        builder.addAssertable(stats.getStatsType(), getName(), stats);
        return builder.build();
    }

    private Stats executeConsecutively(
            TestExecutor<?, ?, Runnable, MixedAssertableHolder> producer) {
        Stats joinStats = null;
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
                Stats stats = (Stats) holder.getAssertable();
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
