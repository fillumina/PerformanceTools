package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.suite.ParameterContainer;
import com.fillumina.performance.producer.suite.ParametrizedExecutor;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import java.util.Map;

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
    public abstract void addParameters(final ParameterContainer<T> parameters);

    /**
     * Declares tests. Each test is executed in place.
     * <pre>
     * executor.executeTest("test name", new ParametrizedRunnable<Integer>() {
     *     public void call(final Integer param) {
     *          // test code...
     *     }
     * });
     * </pre>
     */
    public abstract void executeTests(final ParametrizedExecutor<T> executor);

    /**
     * To discriminate between different tests use test's and parameter's names:
     * <pre>
     * assertion.forExecution(<b>TEST_NAME</b>).
     *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
     * </pre>
     */
    public abstract void addAssertions(final SuiteExecutionAssertion assertion);

    /** Called at the end of the execution, use for assertions or printouts. */
    public void onAfterExecution(
            final Map<String, LoopPerformances> performanceMap) {}

    @Override
    public void executePerformanceTest(
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
