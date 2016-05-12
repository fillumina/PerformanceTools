package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This template adds to each test a parameter and an item of a sequence.
 * <p>
 * The tests are created from the parameters (each new test will have a
 * different parameter and adopt the parameter's name) and there will be many
 * rounds each one named after the name of the test combined with the
 * string representation of the sequence item.
 * <p>
 * The performances returned are the average of the performances over all the
 * items of the sequence while intermediate performances are calculated on the
 * actual sequence item.
 * <p>
 * By this way it is possible to test different {@code Map}s (parameters)
 * with different sizes (sequence).
 * <p>
 * To create the name of the test use the static method
 * {@link #testName(String, Object) }.
 *
 * @author Francesco Illuminati
 */
public abstract class AutoParametrizedSequencePerformanceTemplate<P,S>
        extends AbstractPerformanceTemplate
            <Map<String, Map<String, PerformanceStats>>,
             ParametrizedSequenceTestable<P,S>> {

    private final PerformanceAssertion assertion =
            AssertPerformance.withTolerance(10); //TODO fixed to 10??

    private final TestConfigurator configuration = new TestConfigurator();


    public AutoParametrizedSequencePerformanceTemplate() {
        super();
    }

    @Override
    protected void initConfiguration(TestConfigurator configuration) {
        configuration
                .setSamplesPerStep(60)
                .setMinConfidence(0.7)
                .setMaxPercentageMargin(0.05)
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

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final Map<String, PerformanceStats> performanceMap) {}

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        final String seqName = seqItem == null ? null : seqItem.toString();
        return ParametrizedSequencePerformanceSuite.createName(name, seqName);
    }

    @Override
    public void executePerformanceTest(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {

        initConfiguration(configuration);
        config(configuration);

        PerformanceTimer producer = configuration.createPerformanceTimer();

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration,
                        iterationConsumer, resultConsumer);

        ParametrizedPerformanceSuite<P> parametrizedSuite =
                new ParametrizedPerformanceSuite<>();
        addParameters(parametrizedSuite);
        parametrizedSuite.instrument(pe);

        ParametrizedSequencePerformanceSuite<P,S> sequencedSuite =
                new ParametrizedSequencePerformanceSuite<>();
        addSequence(sequencedSuite);
        sequencedSuite.instrument(parametrizedSuite);

        addTests(sequencedSuite);
        addAssertions(assertion);

        final PerformanceStats stats = pe
                .execute()
                .use(assertion) //TODO check assertion
                .getPerformance();

        onAfterExecution(stats);
    }

}
