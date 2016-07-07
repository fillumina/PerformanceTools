package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.assertion.AssertableMultiTest;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemConsumptionExecutor;
import com.fillumina.performance.mem.MemSampleLineStringGenerator;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemStatsStringGenerator;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.mem.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.progression.SpeedProgressionStringGenerator;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StringHelper;
import com.fillumina.performance.util.TableFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate<ParametrizedSequenceTestable<P,S>> {

    @Override
    protected void initConfiguration(TestConfiguration configuration) {
        configuration.speed()
                .setSamplesPerStep(60)
                .setMinConfidence(0.7)
                .setMaxPercentageMargin(5)
                .setTimeout(120, TimeUnit.SECONDS);
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

    /**
     * Adds a sequence to tests.
     * <pre>
     * sequences.setSequence('x', 'y', 'z');
     * </pre>
     */
    public abstract void addSequence(final SequenceContainer<S> sequences);

    public abstract void addAssertions(ParametrizedSequenceAssertion assertion);

    @Override
    protected int maxVerobosity() {
        return 4;
    }

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return StringHelper.createName(name, seqName);
    }

    @Override
    public void executePerformanceTest(int verbosity) {

        TestConfiguration configuration = new TestConfiguration();
        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(verbosity, configuration);

        ParametrizedSequenceAssertion assertion =
                new ParametrizedSequenceAssertion();
        addAssertions(assertion);

        PerformanceHolder<Map<ComposedName, Map<ComposedName, SpeedStats>>>
                speedStats = execSpeed(verbosity, configuration, assertion);

        PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
                usedMemStats = executeMem(verbosity,
                        new UsedMemConsumptionExecutor(),
                        configuration.usedMem(),
                        assertion.getUsedMemoryAssertions());

        PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
                allocatedMemStats = executeMem(verbosity,
                        new AllocatedMemConsumptionExecutor(),
                        configuration.allocatedMem(),
                        assertion.getAllocatedMemoryAssertions());

        printResults(verbosity, speedStats, usedMemStats, allocatedMemStats);
        printAssertions(verbosity, assertion, speedStats, usedMemStats,
                allocatedMemStats);
    }

    private PerformanceHolder<Map<ComposedName, Map<ComposedName, SpeedStats>>>
                execSpeed(int verbosity,
            TestConfiguration configuration,
            ParametrizedSequenceAssertion assertion) {
        if (!configuration.speed().isActive()) {
            return null;
        }

        PerformanceTimer producer = configuration.speed()
                .createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration,
                        verbosity - 2);

        ParametrizedPerformanceSuite<P,SpeedStats> parametrizedSpeedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(parametrizedSpeedSuite);
        parametrizedSpeedSuite.addPerformanceConsumerIf(verbosity > 1,
                SpeedProgressionStringGenerator.
                        <Map<ComposedName,SpeedStats>> parametrizedViewer());
        parametrizedSpeedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S,SpeedStats> sequencedSpeedSuite =
                SpeedSuite.<P,S>parametrizedSequenceSuite();
        addSequence(sequencedSpeedSuite);
        sequencedSpeedSuite.instrument(parametrizedSpeedSuite);

        addTests(sequencedSpeedSuite);

        return sequencedSpeedSuite
                .performGarbageCollection(
                        configuration.speed().garbageCollectorMillis)
                .addPerformanceConsumerIf(verbosity > 0,
                        SpeedProgressionStringGenerator.
                                parametrizedSequenceViewer())
                .setName(configuration.getTestName())
                .execute()
                .check(assertion.getSpeedAssertions());
    }

    private PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
         executeMem(
            int verbosity,
            MemConsumptionExecutor executor,
            MemConfiguration memConf,
            AssertParametrizedSequencePerformance<Void, MemStats> assertion) {
        if (!memConf.isActive()) {
            return null;
        }

        executor.addPerformanceConsumerIf(verbosity > 0,
                MemSampleLineStringGenerator.VIEWER);

        MemAnalyzer analyzer = new MemAnalyzer(executor,
            memConf.getSamples(), memConf.getStdFilterFactor());

        ParametrizedPerformanceSuite<P,MemStats> parametrizedMemSuite =
                MemSuite.<P>parametrizedSuite();
        addParameters(parametrizedMemSuite);
        parametrizedMemSuite.addPerformanceConsumerIf(verbosity > 2,
                MemStatsStringGenerator.parametrizedViewer());
        parametrizedMemSuite.instrument(analyzer);

        ParametrizedSequencePerformanceSuite<P,S,MemStats> sequencedMemSuite =
                MemSuite.<P,S>parametrizedSequenceSuite();
        sequencedMemSuite.instrument(parametrizedMemSuite);
        sequencedMemSuite.addPerformanceConsumerIf(verbosity > 3,
                MemStatsStringGenerator.parametrizedSequenceViewer());
        addSequence(sequencedMemSuite);
        addTests(sequencedMemSuite);
        return sequencedMemSuite
                .execute()
                .check(assertion);
    }

    private void printAssertions(int verbosity,
            ParametrizedSequenceAssertion assertion,
            PerformanceHolder<Map<ComposedName, Map<ComposedName, SpeedStats>>>
                    speedStats,
            PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
                    usedMemStats,
            PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
                    allocatedMemStats) {
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
            AssertParametrizedSequencePerformance<Void, A> statsAssertion,
            PerformanceHolder<Map<ComposedName, Map<ComposedName, A>>> stats) {
        if (statsAssertion == null) {
            return "";
        }
        return statsAssertion.toString(stats.getPerformance());
    }
}
