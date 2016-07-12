package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoProgressionPerformanceTemplate
        extends AbstractPerformanceTemplate<Testable> {

    public abstract void addAssertions(ProgressionAssertion assertion);

    @Override
    public void executePerformanceTest(int verbosity) {

        TestConfiguration configuration = new TestConfiguration();
        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(verbosity, configuration);

        ProgressionAssertion assertion = new ProgressionAssertion();
        addAssertions(assertion);

        final PerformanceHolder<SpeedStats> speedStats = executeSpeed(
                configuration, assertion, verbosity);

        final PerformanceHolder<MemStats> usedMemStats = executeMem(
                new UsedMemConsumptionExecutor(),
                configuration.getUsedMem(),
                assertion.getUsedMemoryAssertions(),
                verbosity)
                .createWithFormatter(MemStatsTableStringGenerator.USED_INSTANCE);

        final PerformanceHolder<MemStats> allocatedMemStats = executeMem(
                new AllocatedMemConsumptionExecutor(),
                configuration.getAllocatedMem(),
                assertion.getAllocatedMemoryAssertions(),
                verbosity)
                .createWithFormatter(MemStatsTableStringGenerator.ALLOCATED_INSTANCE);

        printResults(verbosity, speedStats, usedMemStats, allocatedMemStats);
        printAssertion(verbosity, assertion,
                speedStats, usedMemStats, allocatedMemStats);
    }

    private PerformanceHolder<SpeedStats> executeSpeed(
            TestConfiguration configuration,
            ProgressionAssertion assertion,
            int verbosity) {
        if (!configuration.getSpeed().isActive()) {
            return null;
        }

        PerformanceTimer producer = configuration.getSpeed()
                .createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration, verbosity);
        addTests(pe);

        return pe
                .performGarbageCollection(configuration.getSpeed()
                        .garbageCollectorMillis)
                .setName(configuration.getTestName())
                .execute()
                .check(assertion.getSpeedAssertions());
    }

    private PerformanceHolder<MemStats> executeMem(
            MemConsumptionExecutor executor,
            MemConfiguration memConf,
            StatsAssertion<MemStats> statsAssertion,
            int verbosity) {
        if (!memConf.isActive()) {
            return PerformanceHolder.<MemStats>empty();
        }

        MemAnalyzer analyzer = new MemAnalyzer(executor,
                            memConf.getSamples(),
                            memConf.getStdFilterFactor());
        addTests(analyzer);
        return analyzer
                .addMemProgressionStatusListener(
                        new ConsoleMemProgressionListener(verbosity))
                .execute()
                .check(statsAssertion);
    }

    private void printAssertion(int verbosity,
            ProgressionAssertion assertion,
            PerformanceHolder<SpeedStats> speedStats,
            PerformanceHolder<MemStats> usedMemStats,
            PerformanceHolder<MemStats> allocatedMemStats) {
        if (verbosity == 0) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(assertionToString(
                assertion.getSpeedAssertions(),
                "Speed",
                speedStats));
        buf.append(assertionToString(
                assertion.getUsedMemoryAssertions(),
                "Used Memory",
                usedMemStats));
        buf.append(assertionToString(
                assertion.getAllocatedMemoryAssertions(),
                "Allocated Memory",
                allocatedMemStats));
        if (buf.length() != 0) {
            System.out.println("\n" + TableFormatter.title("ASSERTIONS", '-') +
                    buf.toString());
        }
    }

    private <A extends AssertableMultiTest> String assertionToString(
            StatsAssertion<A> statsAssertion,
            String title,
            PerformanceHolder<A> stats) {
        if (statsAssertion == null) {
            return "";
        }
        return System.lineSeparator() + title + System.lineSeparator() +
                statsAssertion.toString(stats.getPerformance());
    }
}
