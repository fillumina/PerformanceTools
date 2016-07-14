package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.infrastructure.StatsTree;
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
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <ParametrizedTestable<P>,
             AssertParametrizedPerformance<Void, SpeedStats>,
             AssertParametrizedPerformance<Void, MemStats>> {

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
    protected MixedAssertion<AssertParametrizedPerformance<Void, SpeedStats>,
                   AssertParametrizedPerformance<Void, MemStats>>
            createAndInitAssertion() {
        ParametrizedAssertion assertion = new ParametrizedAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected StatsTree<SpeedStats> executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParametrizedPerformance<Void, SpeedStats> assertions,
            AutoProgressionPerformanceInstrumenter progression) {

        ParametrizedPerformanceSuite<P,SpeedStats> speedSuite =
                SpeedSuite.<P>parametrizedSuite();
        addParameters(speedSuite);
        speedSuite.instrument(progression);

        addTests(speedSuite);

        return new StatsTree<>(ComposedName.create(testName),
            speedSuite
                .performGarbageCollection(
                        speedConfiguration.garbageCollectorMillis)
                .setName(testName)
                .execute()
                .check(assertions)
                .getPerformance());
    }

    @Override
    protected StatsTree<MemStats> executeMem(String testName,
            AssertParametrizedPerformance<Void, MemStats> assertion,
            MemAnalyzer analyzer) {

        ParametrizedPerformanceSuite<P, MemStats> memSuite =
                MemSuite.parametrizedSuite();
        addParameters(memSuite);
        addTests(memSuite);

        return new StatsTree<>(ComposedName.create(testName),
            memSuite
                .instrument(analyzer)
                .execute()
                .check(assertion)
                .getPerformance());
    }
}
