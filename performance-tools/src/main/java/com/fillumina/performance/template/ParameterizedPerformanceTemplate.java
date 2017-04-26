package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterized;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.MemSuite;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.AutoProgressionStatsProducer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedPerformanceSuite;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class ParameterizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <ParameterizedTestable<P>,
             PHolder<SpeedStats>,
             PHolder<MemStats>,
             AssertParameterized<ParameterizedMixedAssertion, SpeedStats>,
             AssertParameterized<ParameterizedMixedAssertion, MemStats>> {

    @Override
    protected void initConfiguration(TestConfiguration configuration) {
        configuration.getSpeed()
                .setMaxPercentageMargin(3);
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
    public abstract void addAssertions(ParameterizedMixedAssertion assertion);

    @Override
    protected void appendConfigParameters(Appendable appendable) {
        final TableFormatter tf = new TableFormatter();
        tf.header("paramenters", TableFormatter.Alignment.LEFT, '-');

        addParameters(new ParameterContainer<P>() {
            @Override
            public ParameterContainer<P> addParameter(String name, P param) {
                tf.cell(name, ":").cell(param).endl();
                return this;
            }
        });
        try {
            appendable.append(tf.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected MixedAssertion
                <AssertParameterized<ParameterizedMixedAssertion, SpeedStats>,
                 AssertParameterized<ParameterizedMixedAssertion, MemStats>>
                createAndInitAssertion() {
        ParameterizedMixedAssertion assertion = new ParameterizedMixedAssertion();
        addAssertions(assertion);
        return assertion;
    }

    @Override
    protected PHolder<PHolder<SpeedStats>> executeSpeed(String testName,
            SpeedConfiguration speedConfiguration,
            AssertParameterized<ParameterizedMixedAssertion, SpeedStats> assertions,
            AutoProgressionStatsProducer progression) {

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
    protected PHolder<PHolder<MemStats>> executeMem(String testName,
            AssertParameterized<ParameterizedMixedAssertion, MemStats>  assertion,
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
