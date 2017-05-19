package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StatsProducer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes tests sequentially and returns them as an aggregate statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducer
        extends AbstractPerformanceProducer
            <ConsecutiveExecutorStatsProducer, SpeedStats, Runnable>
        implements Instrumenter<StatsProducer<SpeedStats>>,
                   StatsProducer<SpeedStats> {

    private final boolean consecutiveExecution;
    private StatsProducer<SpeedStats> producer;

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
    public Instrumenter<StatsProducer<SpeedStats>> instrument(
            StatsProducer<SpeedStats> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<SpeedStats>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    @Override
    public PHolder<SpeedStats> execute() {
        if (!consecutiveExecution) {
            return producer.execute();
        }

        Map<TName,Runnable> tests = getTests();
        List<SpeedStats> results = new ArrayList<>(tests.size());
        for (Map.Entry<TName, Runnable> entry : tests.entrySet()) {
            producer.clearTests();
            producer.setName(entry.getKey());
            producer.addTest(entry.getKey(), entry.getValue());

            PHolder<SpeedStats> holder = producer.execute();

            SpeedStats stats = holder.getAssertable();
            results.add(stats);
        }
        producer.clearTests();

        SpeedStats global = SpeedStats.joinAll(results);

        return new PHolder<>(getName(), global);
    }

}
