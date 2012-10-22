package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSuitePerformanceTemplate<T> {

    private final AutoProgressionPerformanceBuilder executorBuilder =
            new AutoProgressionPerformanceBuilder();

    public void testWithOutput() {
        executorBuilder.setPrintOutStdDeviation(true);
        executeSuite(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    @Test
    public void test() {
        executeSuite(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    public abstract void init(final AutoProgressionPerformanceBuilder config);

    public abstract void addObjects(final ParametrizedPerformanceSuite<T> suite);

    public abstract void addAssertions(final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(final ParametrizedPerformanceSuite<T> suite);

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(final ParametrizedPerformanceSuite<T> suite) {}

    // TODO is iterationConsumer really needed? it is not shown at all!
    public void executeSuite(
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
