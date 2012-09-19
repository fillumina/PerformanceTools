package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import org.junit.Test;

/**
 *
 * @author fra
 */
public abstract class JUnitSequenceSuitePerformanceTemplate<P,S> {

    private final AutoProgressionPerformanceBuilder perfInstrumenter =
            new AutoProgressionPerformanceBuilder();

    public void testWithOutput() {
        perfInstrumenter.setPrintOutStdDeviation(true);
        executeSuite(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    @Test
    public void test() {
        executeSuite(NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE);
    }

    public abstract void init(final AutoProgressionPerformanceBuilder config);

    public abstract void addObjects(
            final ParametrizedSequencePerformanceSuite<P,S> suite);

    public abstract void addSequence(
            final ParametrizedSequencePerformanceSuite<P, S> suite);

    public abstract void addAssertions(
            final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(
            final ParametrizedSequencePerformanceSuite<P,S> suite);

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

        addObjects(suite);

        addSequence(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        executeTests(suite);

        onAfterExecution(suite);
    }

    public final String createTestName(final Object name, final Object seq) {
        return ParametrizedSequencePerformanceSuite.createName(name, seq);
    }
}
