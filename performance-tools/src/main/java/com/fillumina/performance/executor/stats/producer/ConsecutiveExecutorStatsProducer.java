package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.tname.TName;

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
    public MixedStatsHolder get() {
        if (!consecutiveExecution) {
            return executeProducer();
        }

        TestExecutor<?,?,Runnable,MixedStatsHolder> producer =
                getProducer();

        Stats stats = executeConsecutively(producer);
        producer.clearTests();

        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        builder.addAssertable(stats.getStatsType(), getName(), stats);
        return builder.build();
    }

    private Stats executeConsecutively(
            TestExecutor<?, ?, Runnable, MixedStatsHolder> producer) {
        final Holder<Stats> joinStats = new Holder<>();
        getTests().entrySet().forEach(entry -> {
            final TName name = entry.getKey();
            final Runnable test = entry.getValue();

            producer.clearTests();
            producer.setName(name);
            producer.addTest(name, test);

            MixedStatsHolder mixedHolder = producer.get();

            mixedHolder.getStatsMap().values().stream()
                    .map((holder) -> holder.getStats())
                    .forEachOrdered((stats) -> {
                        if (joinStats.isNull()) {
                            joinStats.setValue(stats);
                        } else {
                            joinStats.setValue(joinStats.getValue().join(stats));
                        }
                    });
        });
        return joinStats.getValue();
    }
}
