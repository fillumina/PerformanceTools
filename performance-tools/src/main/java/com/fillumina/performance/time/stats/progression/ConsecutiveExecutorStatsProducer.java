package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceInstrumentable;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
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
            <ConsecutiveExecutorStatsProducer, TimeStats> {
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
    public PHolder<TimeStats> execute() {
        if (!consecutiveExecution) {
            return executeProducer();
        }

        Map<TName,Runnable> tests = getTests();
        List<TimeStats> results = new ArrayList<>(tests.size());
        StatsProducer<TimeStats> producer = getProducer();
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            producer.clearTests();
            producer.setName(entry.getKey());
            producer.addTest(entry.getKey(), entry.getValue());

            PHolder<TimeStats> holder = producer.execute();

            TimeStats stats = holder.getAssertable();
            results.add(stats);
        }
        producer.clearTests();

        TimeStats global = TimeStats.joinAll(results);

        return new PHolder<>(getName(), global);
    }

}
