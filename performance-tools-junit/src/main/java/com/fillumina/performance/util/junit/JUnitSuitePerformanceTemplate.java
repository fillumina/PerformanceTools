package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;

/**
 *
 * @author fra
 */
//TODO make this class a simple "upgrade" to JUnitAutoProgressionPerformanceTemplate. so that it's easier to pass from one template to another
public abstract class JUnitSuitePerformanceTemplate<T> {

    private final PerformanceInstrumenterBuilder perfInstrumenter =
            new PerformanceInstrumenterBuilder();

    public abstract void init(final PerformanceInstrumenterBuilder builder);

    public abstract void addObjects(final ParametrizedPerformanceSuite<T> suite);

    public abstract void addAssertions(final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(final ParametrizedPerformanceSuite<T> suite);

    public ParametrizedPerformanceSuite<T> executeSuite(
            final PerformanceConsumer resultConsumer) {

        init(perfInstrumenter);

        final ParametrizedPerformanceSuite<T> suite =
                perfInstrumenter.createPerformanceExecutor()
                .instrumentedBy(new ParametrizedPerformanceSuite<T>());

        addObjects(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        executeTests(suite);

        return suite;
    }
}
