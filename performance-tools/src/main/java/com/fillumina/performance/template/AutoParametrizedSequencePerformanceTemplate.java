package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.infrastructure.StatsTree;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
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
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate
            <ParametrizedSequenceTestable<P,S>,
             AssertParametrizedSequencePerformance<Void, SpeedStats>,
             AssertParametrizedSequencePerformance<Void, MemStats>> {

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
    protected MixedAssertion<AssertParametrizedSequencePerformance<Void, SpeedStats>,
                   AssertParametrizedSequencePerformance<Void, MemStats>>
            createAndInitAssertion() {
        ParametrizedSequenceAssertion assertion =
            new ParametrizedSequenceAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected StatsTree<SpeedStats> executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParametrizedSequencePerformance<Void, SpeedStats> assertion,
            AutoProgressionPerformanceInstrumenter progression) {

        ParametrizedPerformanceSuite<P,SpeedStats> parametrizedSpeedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(parametrizedSpeedSuite);
        parametrizedSpeedSuite.instrument(progression);

        ParametrizedSequencePerformanceSuite<P,S,SpeedStats> sequencedSpeedSuite =
                SpeedSuite.<P,S>parametrizedSequenceSuite();
        addSequence(sequencedSpeedSuite);
        sequencedSpeedSuite.instrument(parametrizedSpeedSuite);

        addTests(sequencedSpeedSuite);

        return new StatsTree<>(ComposedName.create(testName),
            sequencedSpeedSuite
                .performGarbageCollection(
                        speedConfiguration.garbageCollectorMillis)
                .setName(testName)
                .execute()
                .check(assertion)
                .getPerformance());
    }

    @Override
    protected StatsTree<MemStats> executeMem(String testName,
            AssertParametrizedSequencePerformance<Void, MemStats> assertion,
            MemAnalyzer analyzer) {

        ParametrizedPerformanceSuite<P,MemStats> parametrizedMemSuite =
                MemSuite.<P>parametrizedSuite();
        addParameters(parametrizedMemSuite);
        parametrizedMemSuite.instrument(analyzer);

        ParametrizedSequencePerformanceSuite<P,S,MemStats> sequencedMemSuite =
                MemSuite.<P,S>parametrizedSequenceSuite();
        sequencedMemSuite.instrument(parametrizedMemSuite);
        addSequence(sequencedMemSuite);
        addTests(sequencedMemSuite);

        return new StatsTree<>(ComposedName.create(testName),
            sequencedMemSuite
                .execute()
                .check(assertion)
                .getPerformance());
    }
}
