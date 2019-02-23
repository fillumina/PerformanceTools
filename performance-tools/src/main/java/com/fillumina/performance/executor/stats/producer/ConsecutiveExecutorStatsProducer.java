package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.TestExecutor;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.pathname.PathName;

/**
 * Executes tests sequentially and returns them as an aggregate statistics.
 * The default executor executes them in an interleaved way to average
 * disturbances but this is not always possible or desirable.
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

        TestExecutor<?,?,Runnable,MixedStatsHolder> producer = getProducer();

        Stats stats = executeConsecutively(producer);
        producer.clearTests();

        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        builder.addStats(getPathName(), stats);
        return builder.build();
    }

    private Stats executeConsecutively(
            TestExecutor<?, ?, Runnable, MixedStatsHolder> producer) {
        final Holder<Stats> joinStats = new Holder<>();
        getTests().forEach((PathName name, Runnable test) -> {
            producer.clearTests();
            producer.setPathName(name);
            producer.addTest(name, test);

            MixedStatsHolder mixedHolder = producer.get();

            mixedHolder.getStatsMap().values().stream()
                    .map(holder -> holder.getStats())
                    .forEachOrdered(stats -> {
                        if (joinStats.isNull()) {
                            joinStats.setValue(stats);
                        } else {
                            joinStats.setValue((Stats)
                                    joinStats.getValue().join(stats));
                        }
                    });
        });
        return joinStats.getValue();
    }
}
