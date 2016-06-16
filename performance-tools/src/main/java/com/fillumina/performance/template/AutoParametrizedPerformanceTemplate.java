package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.suite.assertion.AssertParametrizedSpeed;
import com.fillumina.performance.suite.formatter.StringTableParametrizedStatsFormatter;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <Map<ComposedName, PerformanceStats>,
             ParametrizedTestable<P>> {
    private PerformanceConsumer<Map<ComposedName, PerformanceStats>>
            parametrizedStatConsumer =
            NullPerformanceConsumer.<Map<ComposedName, PerformanceStats>>instance();

    public AutoParametrizedPerformanceTemplate() {
        super();
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    @Override
    public void executeWithIntermediateOutput() {
        parametrizedStatConsumer = StringTableParametrizedStatsFormatter.VIEWER;
        super.executeWithIntermediateOutput();
    }
    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    @Override
    public void executeWithFullOutput() {
        parametrizedStatConsumer = StringTableParametrizedStatsFormatter.VIEWER;
        super.executeWithFullOutput();
    }

    @Override
    protected void initConfiguration(TestConfigurator configuration) {
        configuration
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

    public abstract void addAssertions(AssertParametrizedSpeed assertion);

    /** Called at the end of the execution, use for assertions or printouts. */
    @Override
    public void onAfterExecution(
            final Map<ComposedName, PerformanceStats> performanceMap) {}

    public PerformanceConsumer<Map<ComposedName, PerformanceStats>>
            getParametrizedStatConsumer() {
        return parametrizedStatConsumer;
    }

    @Override
    public void executePerformanceTest(boolean printout) {

        TestConfigurator configuration = new TestConfigurator();
        printOutConfiguration(printout, configuration);
        initConfiguration(configuration);
        config(configuration);

        AssertParametrizedSpeed assertion = new AssertParametrizedSpeed();
        addAssertions(assertion);

        PerformanceTimer performanceTimer =
                configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(performanceTimer, configuration);

        ParametrizedPerformanceSuite<P> suite =
                new ParametrizedPerformanceSuite<>();
        addParameters(suite);
        suite.instrument(pe);

        addTests(suite);

        final Map<ComposedName, PerformanceStats> stats = suite
                .performGarbageCollection(configuration.garbageCollectorMillis)
                .setName(configuration.getName())
                .addPerformanceConsumerIf(printout,
                        getParametrizedStatConsumer())
                .execute()
                .use(assertion)
                .getPerformance();

        printOutAssertion(printout, assertion,
                ComposedName.create(configuration.getName()), stats);

        onAfterExecution(stats);
    }

}
