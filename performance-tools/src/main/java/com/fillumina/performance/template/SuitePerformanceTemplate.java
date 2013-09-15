package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SuitePerformanceTemplate<T>
        extends SimplePerformanceTemplate {

    private final ProgressionConfigurator executorBuilder =
            new ProgressionConfigurator();

    public SuitePerformanceTemplate() {
        executorBuilder.setPrintOutStdDeviation(true);
    }

    @Override
    public void testWithoutOutput() {
        executorBuilder.setPrintOutStdDeviation(false);
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    public abstract void init(final ProgressionConfigurator config);

    public abstract void addObjects(final ParametrizedPerformanceSuite<T> suite);

    public abstract void addAssertions(final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(final ParametrizedPerformanceSuite<T> suite);

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(final ParametrizedPerformanceSuite<T> suite) {}

    // TODO is iterationConsumer really needed? it is not shown at all!
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

        addObjects(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        executeTests(suite);

        onAfterExecution(suite);
    }
}
