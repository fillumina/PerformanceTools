package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedPerformanceSuite;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParameterizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <ParameterizedTestable<P>,
             Map<ComposedName, SpeedStats>,
             Map<ComposedName, MemStats>,
             AssertParameterizedPerformanceImpl<ParameterizedAssertion, SpeedStats>,
             AssertParameterizedPerformanceImpl<ParameterizedAssertion, MemStats>> {

    @Override
    protected void initConfiguration(TestConfiguration configuration) {
        configuration.getSpeed()
                .setSamplesPerStep(100)
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
     */
    public abstract void addParameters(final ParameterContainer<P> params);

    /**
     * Adds assertions to be checked <i>after</i> test execution.
     * <pre>
     *     assertion.speed().forTest(FIRST,
     *             AssertSpeed.withTolerance(5)
     *                 .assertOrder("1").lessThan("2"));
     *     assertion.usedMem().forTest(SECOND,
     *             AssertMemory.withTolerance(5)
     *                 .assertValue("1").sameAs(56));
     * </pre>
     */
    public abstract void addAssertions(ParameterizedAssertion assertion);

    @Override
    protected MixedAssertion<AssertParameterizedPerformanceImpl
                        <ParameterizedAssertion, SpeedStats>,
                   AssertParameterizedPerformanceImpl
                        <ParameterizedAssertion, MemStats>>
            createAndInitAssertion() {
        ParameterizedAssertion assertion = new ParameterizedAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected TreeHolder<SpeedStats, Map<ComposedName, SpeedStats>>
        executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParameterizedPerformanceImpl
                    <ParameterizedAssertion, SpeedStats> assertions,
            AutoProgressionPerformanceInstrumenter progression) {

        ParameterizedPerformanceSuite<P,SpeedStats> speedSuite =
                SpeedSuite.<P>parameterizedSuite();
        addParameters(speedSuite);
        speedSuite.instrument(progression);

        addTests(speedSuite);

        return speedSuite
                    .performGarbageCollection(
                            speedConfiguration.garbageCollectorMillis)
                    .setName(testName)
                    .execute()
                    .check(assertions);
    }

    @Override
    protected TreeHolder<MemStats, Map<ComposedName, MemStats>>
        executeMem(String testName,
            AssertParameterizedPerformanceImpl
                    <ParameterizedAssertion, MemStats> assertion,
            MemAnalyzer analyzer) {

        ParameterizedPerformanceSuite<P, MemStats> memSuite =
                MemSuite.parameterizedSuite();
        addParameters(memSuite);
        addTests(memSuite);

        return memSuite
                    .instrument(analyzer)
                    .setName(testName)
                    .execute()
                    .check(assertion);
    }
}
