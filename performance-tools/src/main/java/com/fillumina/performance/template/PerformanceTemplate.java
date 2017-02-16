package com.fillumina.performance.template;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class PerformanceTemplate
        extends AbstractPerformanceTemplate
            <Testable,
            SpeedStats,
            MemStats,
            StatsAssertion<ProgressionAssertion,SpeedStats>,
            StatsAssertion<ProgressionAssertion,MemStats>> {

    public abstract void addAssertions(ProgressionAssertion assertions);

    @Override
    protected void appendConfigParameters(Appendable appendable) {
        // no extra params to show
    }

    @Override
    protected MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                    StatsAssertion<ProgressionAssertion,MemStats>>
            createAndInitAssertion() {
        ProgressionAssertion assertion = new ProgressionAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected PHolder<SpeedStats> executeSpeed(
            String testName,
            SpeedConfiguration speedConfiguration,
            StatsAssertion<ProgressionAssertion,SpeedStats> speedAssertions,
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
    protected PHolder<MemStats> executeMem(String testName,
            StatsAssertion<ProgressionAssertion,MemStats> assertion,
            MemAnalyzer analyzer) {

        addTests(analyzer);

        return analyzer
                .setName(testName)
                .execute()
                .check(assertion);
    }
}
