package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoProgressionPerformanceTemplate
        extends AbstractPerformanceTemplate<SpeedStats, Testable> {

    public abstract void addAssertions(StatsAssertion<SpeedStats> assertion);

    @Override
    public void executePerformanceTest(boolean printout) {

        TestConfigurator configuration = new TestConfigurator();

        StatsAssertion<SpeedStats> assertion =
                AssertPerformance.withTolerance(10);

        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(printout, configuration);

        PerformanceTimer producer = configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration);
        addTests(pe);
        addAssertions(assertion);

        final SpeedStats stats = pe
                .performGarbageCollection(configuration.garbageCollectorMillis)
                .setName(configuration.getName())
                .execute()
                .use(assertion)
                .getPerformance();

        printOutAssertion(printout, assertion,
                ComposedName.create(configuration.getName()), stats);

        onAfterExecution(stats);
    }
}
