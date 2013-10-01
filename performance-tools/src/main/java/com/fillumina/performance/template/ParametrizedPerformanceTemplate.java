package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedExecutor;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import java.util.Map;

/**
 * It works just like the {@link AutoProgressionPerformanceTemplate} but
 * allows to add a parameter to each test. This means that you can run
 * the same test against different objects and so automatically
 * creating different tests. I.e. you can test the relative speed of different
 * type of {@code List}s.
 * <p>
 * Differently from {@link AutoProgressionPerformanceTemplate} each
 * test is executed on the spot (where it is created) and the results
 * returned so there isn't a global {@code execute()} code for all tests.
 * To discriminate between different tests each has a name that can
 * be used in {@code assertion.forExecution(TEST_NAME)} and each parameter
 * is named too so to use:
 * <pre>
 * assertion.forExecution(<b>TEST_NAME</b>).
 *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
 * </pre>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class ParametrizedPerformanceTemplate<T>
        extends SimplePerformanceTemplate {

    private final ProgressionConfigurator executorBuilder =
            new ProgressionConfigurator();

    public ParametrizedPerformanceTemplate() {
        executorBuilder.setPrintOutStdDeviation(true);
    }

    /** This is the best candidate method to call in case of a unit test. */
    @Override
    public void testWithoutOutput() {
        executorBuilder.setPrintOutStdDeviation(false);
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
    public abstract void addParameters(final ParametersContainer<T> parameters);

    /**
     * To discriminate between different tests use test's and parameter's names:
     * <pre>
     * assertion.forExecution(<b>TEST_NAME</b>).
     *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
     * </pre>
     */
    public abstract void addAssertions(final SuiteExecutionAssertion assertion);

    /**
     * Declares tests. Each test is executed in place.
     * <pre>
     * executor.executeTest(TEST, new ParametrizedRunnable<Integer>() {
     *     public void call(final Integer param) {
     *          // test code...
     *     }
     * });
     * </pre>
     */
    public abstract void executeTests(final ParametrizedExecutor<T> executor);

    /** Called at the end of the execution, use for assertions or printouts. */
    public void onAfterExecution(
            final Map<String, LoopPerformances> performanceMap) {}

    @Override
    protected void executePerformanceTest(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        init(executorBuilder);

        executorBuilder.setIterationConsumer(iterationConsumer);

        final ParametrizedPerformanceSuite<T> suite =
                executorBuilder.create()
                .addPerformanceConsumer(iterationConsumer)
                .instrumentedBy(new ParametrizedPerformanceSuite<T>());

        addParameters(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        executeTests(suite);

        onAfterExecution(suite.getTestLoopPerformances());
    }
}
