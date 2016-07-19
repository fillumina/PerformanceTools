package com.fillumina.performance.template;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoProgressionPerformanceTemplate
        extends AbstractPerformanceTemplate
            <Testable,
            SpeedStats,
            MemStats,
            StatsAssertion<SpeedStats>,
            StatsAssertion<MemStats>> {

    public abstract void addAssertions(ProgressionAssertion assertions);

    @Override
    protected MixedAssertion<StatsAssertion<SpeedStats>, StatsAssertion<MemStats>>
            createAndInitAssertion() {
        ProgressionAssertion assertion = new ProgressionAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected TreeHolder<SpeedStats, SpeedStats> executeSpeed(
            String testName,
            SpeedConfiguration speedConfiguration,
            StatsAssertion<SpeedStats> speedAssertions,
            AutoProgressionPerformanceInstrumenter progression) {

        addTests(progression);

        return progression
                .performGarbageCollection(
                        speedConfiguration.garbageCollectorMillis)
                .setName(testName)
                .execute()
                .check(speedAssertions);
    }

    @Override
    protected TreeHolder<MemStats, MemStats> executeMem(String testName,
            StatsAssertion<MemStats> assertion,
            MemAnalyzer analyzer) {

        addTests(analyzer);

        return analyzer
                .setName(testName)
                .execute()
                .check(assertion);
    }
}
