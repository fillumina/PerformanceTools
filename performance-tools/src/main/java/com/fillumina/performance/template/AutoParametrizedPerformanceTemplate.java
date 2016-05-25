package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.suite.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * It works just like the {@link AutoProgressionPerformanceTemplate} but it
 * allows to add a parameter to each test. This means that you can run
 * the same code against different objects and so automatically
 * creating different tests. (i.e. you can test the relative speed of different
 * type of {@code List}s by using the same test code and passing different
 * type of list to it).
 * <p>
 * Differently from {@link AutoProgressionPerformanceTemplate} each
 * test is executed on the spot (where it is created) and the results
 * returned so there isn't a global {@code execute()} code for all tests.
 * To discriminate between different tests each has a name that can
 * be used in {@code assertion.forExecution(TEST_NAME)} and each parameter
 * is named too:
 * <pre>
 * assertion.forExecution(<b>TEST_NAME</b>).
 *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
 * </pre>
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedPerformanceTemplate<P>
        extends AbstractPerformanceTemplate
            <Map<ComposedName, PerformanceStats>,
             ParametrizedTestable<P>> {

    public AutoParametrizedPerformanceTemplate() {
        super();
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

    public abstract void addAssertions(AssertParametrizedPerformance<?> assertion);

    /** Called at the end of the execution, use for assertions or printouts. */
    @Override
    public void onAfterExecution(
            final Map<ComposedName, PerformanceStats> performanceMap) {}

    @Override
    public void executePerformanceTest(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {

        TestConfigurator configuration = new TestConfigurator();

        initConfiguration(configuration);
        config(configuration);

        PerformanceTimer producer = configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration,
                        iterationConsumer, resultConsumer);

        ParametrizedPerformanceSuite<P> suite =
                new ParametrizedPerformanceSuite<>();
        addParameters(suite);
        suite.instrument(pe);

        addTests(suite);

        AssertParametrizedPerformance<?> assertion =
                new AssertParametrizedPerformance<>();
        addAssertions(assertion);

        final Map<ComposedName, PerformanceStats> stats = suite
                .performGarbageCollection()
                .execute()
                .use(assertion)
                .getPerformance();

        onAfterExecution(stats);
    }

}
