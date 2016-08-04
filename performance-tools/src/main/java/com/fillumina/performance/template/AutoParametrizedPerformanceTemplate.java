package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformanceImpl;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <ParametrizedTestable<P>,
             Map<ComposedName, SpeedStats>,
             Map<ComposedName, MemStats>,
             AssertParametrizedPerformanceImpl<Void, SpeedStats>,
             AssertParametrizedPerformanceImpl<Void, MemStats>> {

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
     * @param params
     */
    public abstract void addParameters(final ParameterContainer<P> params);

    public abstract void addAssertions(ParametrizedAssertion assertion);

    @Override
    protected MixedAssertion<AssertParametrizedPerformanceImpl<Void, SpeedStats>,
                   AssertParametrizedPerformanceImpl<Void, MemStats>>
            createAndInitAssertion() {
        ParametrizedAssertion assertion = new ParametrizedAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected TreeHolder<SpeedStats, Map<ComposedName, SpeedStats>>
        executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParametrizedPerformanceImpl<Void, SpeedStats> assertions,
            AutoProgressionPerformanceInstrumenter progression) {

        ParametrizedPerformanceSuite<P,SpeedStats> speedSuite =
                SpeedSuite.<P>parametrizedSuite();
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
            AssertParametrizedPerformanceImpl<Void, MemStats> assertion,
            MemAnalyzer analyzer) {

        ParametrizedPerformanceSuite<P, MemStats> memSuite =
                MemSuite.parametrizedSuite();
        addParameters(memSuite);
        addTests(memSuite);

        return memSuite
                    .instrument(analyzer)
                    .setName(testName)
                    .execute()
                    .check(assertion);
    }
}
