package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.AllocatedMemStatsStringGenerator;
import com.fillumina.performance.mem.strgen.UsedMemStatsStringGenerator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.formatter.StringHelper;
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
        configuration.getSpeed()
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
    public abstract void addSequence(final SequenceContainer<S> sequence);

    public abstract void addAssertions(ParametrizedSequenceAssertion assertions);

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
                usedMemStats = executeMem(verbosity, "used",
                        new UsedMemConsumptionExecutor(),
                        configuration.getUsedMem(),
                        assertion.getUsedMemoryAssertions())
                .createWithFormatter(
                        UsedMemStatsStringGenerator.parametrizedSequence());


        PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
                allocatedMemStats = executeMem(verbosity, "allocated",
                        new AllocatedMemConsumptionExecutor(),
                        configuration.getAllocatedMem(),
                        assertion.getAllocatedMemoryAssertions())
                .createWithFormatter(
                        AllocatedMemStatsStringGenerator.parametrizedSequence());

        printResults(verbosity, speedStats, usedMemStats, allocatedMemStats);
        printAssertions(verbosity,
                speedStats, usedMemStats, allocatedMemStats,
                assertion.getSpeedAssertions(),
                assertion.getUsedMemoryAssertions(),
                assertion.getAllocatedMemoryAssertions());
    }

    private PerformanceHolder<Map<ComposedName, Map<ComposedName, SpeedStats>>>
                execSpeed(int verbosity,
            TestConfiguration configuration,
            ParametrizedSequenceAssertion assertion) {
        if (!configuration.getSpeed().isActive()) {
            return null;
        }

        PerformanceTimer producer = configuration.getSpeed()
                .createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration,
                        verbosity);

        ParametrizedPerformanceSuite<P,SpeedStats> parametrizedSpeedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(parametrizedSpeedSuite);
        parametrizedSpeedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S,SpeedStats> sequencedSpeedSuite =
                SpeedSuite.<P,S>parametrizedSequenceSuite();
        addSequence(sequencedSpeedSuite);
        sequencedSpeedSuite.instrument(parametrizedSpeedSuite);

        addTests(sequencedSpeedSuite);

        return sequencedSpeedSuite
                .performGarbageCollection(
                        configuration.getSpeed().garbageCollectorMillis)
                .setName(configuration.getTestName())
                .execute()
                .check(assertion.getSpeedAssertions());
    }

    private PerformanceHolder<Map<ComposedName, Map<ComposedName, MemStats>>>
         executeMem(int verbosity,
            String memTestType,
            MemConsumptionExecutor executor,
            MemConfiguration memConf,
            AssertParametrizedSequencePerformance<Void, MemStats> assertion) {
        if (!memConf.isActive()) {
            return PerformanceHolder
                    .<Map<ComposedName, Map<ComposedName, MemStats>>>empty();
        }

        MemAnalyzer analyzer = new MemAnalyzer(executor,
                    memConf.getSamples(), memConf.getStdFilterFactor())
                .addPerformanceConsumerIf(verbosity > 1,
                        memConf.getStringGenerator().viewer())
                .addMemProgressionStatusListener(
                        new ConsoleMemProgressionListener(verbosity, memTestType));

        ParametrizedPerformanceSuite<P,MemStats> parametrizedMemSuite =
                MemSuite.<P>parametrizedSuite();
        addParameters(parametrizedMemSuite);
        parametrizedMemSuite.instrument(analyzer);

        ParametrizedSequencePerformanceSuite<P,S,MemStats> sequencedMemSuite =
                MemSuite.<P,S>parametrizedSequenceSuite();
        sequencedMemSuite.instrument(parametrizedMemSuite);
        addSequence(sequencedMemSuite);
        addTests(sequencedMemSuite);
        return sequencedMemSuite
                .execute()
                .check(assertion);
    }
}
