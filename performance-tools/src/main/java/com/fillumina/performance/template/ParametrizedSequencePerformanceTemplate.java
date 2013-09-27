package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequenceContainer;
import java.util.Map;

/**
 * This template adds to each test a parameter and an item of a sequence.
 * The tests are created from the parameters (each new test will have a different
 * parameter and will have the parameter's name) and there will be many
 * steps each one named after the name of the test combined with the
 * string representation of the sequence item.
 * By this way it is possible to test different {@code Map}s (parameters)
 * with different sizes (sequence).
 * The performances returned are the average of the performances for each item
 * of the sequence while intermediate performances are calculated on the
 * actual sequence item.
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
    public abstract void addParameters(final ParametersContainer<?,P> parameters);

    /**
     * Adds a sequence to tests.
     * <pre>
     * sequences.setSequence('x', 'y', 'z');
     * </pre>
     */
    public abstract void addSequence(final SequenceContainer<?, S> sequences);

    /**
     * To discriminate between different tests use test's and parameter's names:
     * <pre>
     * assertion.forExecution(<b>TEST_NAME</b>).
     *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
     * </pre>
     */
    public abstract void addAssertions(final SuiteExecutionAssertion assertion);

    /** @return the test to be executed. */
    public abstract ParametrizedSequenceRunnable<P, S> getTest();

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final Map<String, LoopPerformances> performanceMap) {}

    @Override
    protected void executePerformanceTest(
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

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        suite.executeTest("test", getTest());

        onAfterExecution(suite.getTestLoopPerformances());
    }

    /**
     * Helper to calculate the test name from the name of the test
     * and the name of the sequence item.
     */
    public static String testName(final String name, final Object seq) {
        return ParametrizedSequencePerformanceSuite.createName(name, seq);
    }
}
