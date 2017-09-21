package com.fillumina.performance.executor.progression;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.NamedTestExecutor;
import com.fillumina.performance.executor.stats.AbstractStatsProducerInstrumenter;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
import java.util.List;
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

        Map<TName,Runnable> tests = getTests();
        LinkedMap<Class<? extends Assertable>, List<TimeStats>> results =
                new LinkedMap<>();

        NamedTestExecutor<?,?,Runnable,MixedAssertableHolder> producer =
                getProducer();

        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            producer.clearTests();
            producer.setName(entry.getKey());
            producer.addTest(entry.getKey(), entry.getValue());

            MixedAssertableHolder mixedHolder = producer.get();

            for (Map.Entry<Class<? extends Assertable>, AssertableHolder<?>> e :
                    mixedHolder.getStatsMap().entrySet()) {
                Class<? extends Assertable> type = e.getKey();
                AssertableHolder<?> holder = e.getValue();
                builder.addAssertable(type,
                        holder.getName(), holder.getAssertable());
            }
        }
        producer.clearTests();

        return builder.build();
    }
}
