package com.fillumina.performance.templates;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequencesContainer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SequenceSuitePerformanceTemplate<P,S> {

    private final ProgressionConfigurator perfInstrumenter =
            new ProgressionConfigurator();

    public void testWithDetailedOutput() {
        perfInstrumenter.setPrintOutStdDeviation(true);
        executeSuite(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    public void testWithOutput() {
        perfInstrumenter.setPrintOutStdDeviation(true);
        executeSuite(NullPerformanceConsumer.INSTANCE, StringTableViewer.CONSUMER);
    }

    public void testWithoutOutput() {
        executeSuite(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    public abstract void init(final ProgressionConfigurator config);

    public abstract void addParameters(final ParametersContainer<?,P> parameters);

    public abstract void addSequence(final SequencesContainer<?, S> sequences);

    public abstract void addAssertions(final SuiteExecutionAssertion assertion);

    public abstract ParametrizedSequenceRunnable<P, S> executeTest();

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final ParametrizedSequencePerformanceSuite<P,S> suite) {}

    public void executeSuite(
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

        suite.executeTest("test", executeTest());

        onAfterExecution(suite);
    }

    public static String testName(final Object name, final Object seq) {
        return ParametrizedSequencePerformanceSuite.createName(name, seq);
    }
}
