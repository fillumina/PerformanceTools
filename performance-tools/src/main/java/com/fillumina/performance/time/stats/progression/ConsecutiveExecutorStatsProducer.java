package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractPerformanceInstrumentable;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes tests sequentially and returns them as an aggregate statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducer
        extends AbstractPerformanceInstrumentable
            <ConsecutiveExecutorStatsProducer> {
    private static final long serialVersionUID = 1L;

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
    public MixedAssertableHolder execute() {
        if (!consecutiveExecution) {
            return executeProducer();
        }

        Map<TName,Runnable> tests = getTests();
        LinkedMap<Class<? extends Assertable>, List<TimeStats>> results =
                new LinkedMap<>();
        StatsProducer producer = getProducer();

        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            producer.clearTests();
            producer.setName(entry.getKey());
            producer.addTest(entry.getKey(), entry.getValue());

            MixedAssertableHolder mixedHolder = producer.execute();

            for (Class<? extends Assertable> t : mixedHolder.getTypes()) {
                @SuppressWarnings("unchecked")
                AssertableHolder<TimeStats> holder =
                        (AssertableHolder<TimeStats>) mixedHolder.getStats(t);
                TimeStats stats = holder.getAssertable();
                results.getOrCreate(t, () -> {
                    return new ArrayList<>(tests.size());
                }).add(stats);
            }
        }

        producer.clearTests();

        return createMixedAssertableHolder(results);
    }

    private MixedAssertableHolder createMixedAssertableHolder(
            LinkedMap<Class<? extends Assertable>, List<TimeStats>> results) {

        MixedAssertableHolder.Builder builder =
                MixedAssertableHolder.builder();
        for (Map.Entry<Class<? extends Assertable>, List<TimeStats>> e : results) {
            Class<? extends Assertable> type = e.getKey();
            List<TimeStats> list = e.getValue();
            TName statsName = list.get(0).getTestNames().iterator().next();

            TimeStats global = TimeStats.joinAll(list);
            builder.addAssertableHolder(type, statsName, global);
        }
        return builder.build();
    }

}
