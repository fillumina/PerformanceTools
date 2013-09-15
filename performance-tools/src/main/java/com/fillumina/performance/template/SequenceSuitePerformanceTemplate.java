package com.fillumina.performance.template;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequenceContainer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class SequenceSuitePerformanceTemplate<P,S>
        extends SimplePerformanceTemplate {

    private final ProgressionConfigurator perfInstrumenter =
            new ProgressionConfigurator();

    public SequenceSuitePerformanceTemplate() {
        perfInstrumenter.setPrintOutStdDeviation(true);
    }

    @Override
    public void testWithoutOutput() {
        perfInstrumenter.setPrintOutStdDeviation(false);
        executePerformanceTest(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    public abstract void init(final ProgressionConfigurator config);

    public abstract void addParameters(final ParametersContainer<?,P> parameters);

    public abstract void addSequence(final SequenceContainer<?, S> sequences);

    public abstract void addAssertions(final SuiteExecutionAssertion assertion);

    public abstract ParametrizedSequenceRunnable<P, S> getTest();

    /** Called at the end of the execution, use for assertions. */
    public void onAfterExecution(
            final ParametrizedSequencePerformanceSuite<P,S> suite) {}

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

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        suite.executeTest("test", getTest());

        onAfterExecution(suite);
    }

    public static String testName(final Object name, final Object seq) {
        return ParametrizedSequencePerformanceSuite.createName(name, seq);
    }
}
