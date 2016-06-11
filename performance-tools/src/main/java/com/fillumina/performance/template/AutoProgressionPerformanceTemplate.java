package com.fillumina.performance.template;

import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.assertion.PerformanceStatsAssertion;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoProgressionPerformanceTemplate
        extends AbstractPerformanceTemplate<PerformanceStats, Testable> {

    public abstract void addAssertions(PerformanceStatsAssertion assertion);

    @Override
    public void executePerformanceTest(boolean printout) {

        TestConfigurator configuration = new TestConfigurator();

        PerformanceStatsAssertion assertion =
            AssertPerformance.withTolerance(10);

        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(printout, configuration);

        PerformanceTimer producer = configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration);
        addTests(pe);
        addAssertions(assertion);

        final PerformanceStats stats = pe
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
