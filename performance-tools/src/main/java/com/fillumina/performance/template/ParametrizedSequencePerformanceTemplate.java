package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequenceContainer;
import java.util.Map;

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
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ParametrizedSequencePerformanceTemplate<P,S>
        extends SimplePerformanceTemplate {

    private final ProgressionConfigurator perfInstrumenter =
            new ProgressionConfigurator();

    public ParametrizedSequencePerformanceTemplate() {
        perfInstrumenter.setPrintOutStdDeviation(true);
    }

    /** It's the best choice to use with unit tests. */
    @Override
    public void testWithoutOutput() {
        perfInstrumenter.setPrintOutStdDeviation(false);
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    /**
     * Configures the test. Please note that {@code ProgressionConfigurator}
     * has some sensible defaults.
     * <pre>
     * config.setBaseIterations(1_000)
     *       .setMaxStandardDeviation(5);
     * </pre>
     */
    public abstract void init(final ProgressionConfigurator config);

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
    public abstract void addParameters(final ParametersContainer<P> parameters);

    /**
     * Adds a sequence to tests.
     * <pre>
     * sequences.setSequence('x', 'y', 'z');
     * </pre>
     */
    public abstract void addSequence(final SequenceContainer<?, S> sequences);
    /**
     * Defines the test to be executed. The test will be injected of
     * parameters (creating brand new tests taken the parameters' names)
     * and a sequence item (creating different series of tests).
     * <p>
     * It <b>could</b> be possible to
     * define more than one test but it would be complex to
     * match them with the right assertions (consumers). Use the
     * <i>fluent interface</i> approach if you need to do that: see
     * {@link com.fillumina.performance.PerformanceTimerFactory}.
     * Anyway each tests defined will act in a totally independent way.
     *
     * @return the test to be executed.
     */
    public abstract ParametrizedSequenceRunnable<P, S> getTest();

    /**
     * To discriminate between different tests use test's and parameter's names:
     * <pre>
     * assertion.forExecution(<b>TEST_NAME</b>).
     *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
     * </pre>
     */
    public abstract void addAssertions(
            final AssertionSuiteBuilder assertionBuilder);

    /**
     * The assertions applies to each combination of test + sequence.
     */
    public abstract void addIntermediateAssertions(
            final PerformanceAssertion assertion);

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final Map<String, LoopPerformances> performanceMap) {}

    @Override
    public void executePerformanceTest(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        init(perfInstrumenter);

        perfInstrumenter.setIterationConsumer(iterationConsumer);

        final ParametrizedSequencePerformanceSuite<P,S> suite =
                perfInstrumenter.create()
                .instrumentedBy(new ParametrizedSequencePerformanceSuite<P,S>());

        addParameters(suite);

        addSequence(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertionSuiteBuilder assertionBuilder =
                new AssertionSuiteBuilder();
        addAssertions(assertionBuilder);

        suite.addPerformanceConsumer(
                assertionBuilder.getAssertPerformanceForExecutionSuite());

        final AssertPerformance assertion = new AssertPerformance();
        addIntermediateAssertions(assertion);

        suite.executeTest("test", getTest())
                .use(assertion);

        onAfterExecution(suite.getTestLoopPerformances());
    }

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seqItem) {
        return ParametrizedSequencePerformanceSuite.createName(name, seqItem);
    }
}
