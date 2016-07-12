package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.MemSampleLineStringGenerator;
import com.fillumina.performance.mem.strgen.UsedMemStatsStringGenerator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate<ParametrizedTestable<P>> {

    @Override
    protected void initConfiguration(TestConfiguration configuration) {
        configuration.getSpeed()
                .setSamplesPerStep(100)
                .setMinConfidence(0.7)
                .setMaxPercentageMargin(3)
                .setTimeout(60, TimeUnit.SECONDS);
    }

    /**
     * Adds named parameters to tests.
     * <pre>
     * parameters
     *       .addParameter(NAME_1, VALUE_1)
     *       .addParameter(NAME_2, VALUE_2)
     *       .addParameter(NAME_3, VALUE_3);
     * </pre>
     * @param parameters
     */
    public abstract void addParameters(final ParameterContainer<P> parameters);

    public abstract void addAssertions(ParametrizedAssertion assertion);

    @Override
    public void executePerformanceTest(int verbosity) {

        TestConfiguration configuration = new TestConfiguration();
        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(verbosity, configuration);

        ParametrizedAssertion assertion = new ParametrizedAssertion();
        addAssertions(assertion);

        PerformanceHolder<Map<ComposedName, SpeedStats>> speedStats =
                execSpeed(configuration, assertion, verbosity);

        PerformanceHolder<Map<ComposedName, MemStats>> usedMemStats =
                executeMem(
                    new UsedMemConsumptionExecutor(),
                    configuration.getUsedMem(),
                    assertion.getUsedMemoryAssertions(),
                    verbosity);

        PerformanceHolder<Map<ComposedName, MemStats>> allocatedMemStats =
                executeMem(
                    new AllocatedMemConsumptionExecutor(),
                    configuration.getAllocatedMem(),
                    assertion.getAllocatedMemoryAssertions(),
                    verbosity);

        printResults(verbosity, speedStats, usedMemStats, allocatedMemStats);
        printAssertions(verbosity, assertion,
                speedStats, usedMemStats, allocatedMemStats);
    }

    private PerformanceHolder<Map<ComposedName, SpeedStats>> execSpeed(
            TestConfiguration configuration,
            ParametrizedAssertion assertion,
            int verbosity) {
        if (!configuration.getSpeed().isActive()) {
            return null;
        }
        PerformanceTimer performanceTimer =
                configuration.getSpeed().createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(performanceTimer, configuration,
                        verbosity - 1);

        ParametrizedPerformanceSuite<P,SpeedStats> speedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(speedSuite);
        speedSuite.instrument(pe);

        addTests(speedSuite);

        return speedSuite
                .performGarbageCollection(configuration.getSpeed()
                        .garbageCollectorMillis)
                .setName(configuration.getTestName())
                .execute()
                .check(assertion.getSpeedAssertions());
    }

    private PerformanceHolder<Map<ComposedName, MemStats>> executeMem(
            MemConsumptionExecutor executor,
            MemConfiguration memConf,
            AssertParametrizedPerformance<Void, MemStats> assertion,
            int verbosity) {
        if (!memConf.isActive()) {
            return null;
        }

        executor.addPerformanceConsumerIf(verbosity > 2,
                MemSampleLineStringGenerator.VIEWER);

        MemAnalyzer analyzer = new MemAnalyzer(executor,
                            memConf.getSamples(),
                            memConf.getStdFilterFactor());

        analyzer.addPerformanceConsumerIf(verbosity > 1,
                memConf.getStringGenerator().viewer());

        ParametrizedPerformanceSuite<P, MemStats> memSuite =
                MemSuite.parametrizedSuite();
        addParameters(memSuite);
        addTests(memSuite);

        return memSuite
                .addPerformanceConsumerIf(verbosity > 0,
                        UsedMemStatsStringGenerator.parametrizedViewer())
                .instrument(analyzer)
                .execute()
                .check(assertion);
    }

    private void printAssertions(int verbosity,
            ParametrizedAssertion assertion,
            PerformanceHolder<Map<ComposedName, SpeedStats>> speedStats,
            PerformanceHolder<Map<ComposedName, MemStats>> usedMemStats,
            PerformanceHolder<Map<ComposedName, MemStats>> allocatedMemStats) {
        if (verbosity == 0) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(assertionToString(
                assertion.getSpeedAssertions(),
                speedStats));
        buf.append(assertionToString(
                assertion.getUsedMemoryAssertions(),
                usedMemStats));
        buf.append(assertionToString(
                assertion.getAllocatedMemoryAssertions(),
                allocatedMemStats));
        if (buf.length() != 0) {
            System.out.println("\n" + TableFormatter.title("ASSERTIONS", '=') +
                    buf.toString());
        }

    }

    private <A extends AssertableMultiTest> String assertionToString(
            AssertParametrizedPerformance<Void, A> statsAssertion,
            PerformanceHolder<Map<ComposedName, A>> stats) {
        if (statsAssertion == null) {
            return "";
        }
        return statsAssertion.toString(stats.getPerformance());
    }
}
