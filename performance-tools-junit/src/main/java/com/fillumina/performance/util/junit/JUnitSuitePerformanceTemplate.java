package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class JUnitSuitePerformanceTemplate<T> {

    public abstract void init();

    public abstract void addObjects(final ParametrizedPerformanceSuite<T> suite);

    public abstract void addAssertions(final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(final ParametrizedPerformanceSuite<T> suite);

    public ParametrizedPerformanceSuite<T> executeSuite(
            final PerformanceConsumer resultConsumer) {

        init();

        final ParametrizedPerformanceSuite<T> suite =
                createSingleThreadPerformanceSuite();

        addObjects(suite);

        suite.addPerformanceConsumer(resultConsumer);

        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);

        executeTests(suite);

        return suite;
    }

    protected ParametrizedPerformanceSuite<T>
            createSingleThreadPerformanceSuite() {
        final ParametrizedPerformanceSuite<T> suite =
                PerformanceTimerBuilder.createSingleThread()
                    .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
                        .setSamplesPerMagnitude(15)
                        .setBaseIterations(1_000)
                        .setTimeout(60, TimeUnit.SECONDS)
                        .setMaxStandardDeviation(1.4)
                        .build()
                        .instrumentedBy(new ParametrizedPerformanceSuite<T>());

        return suite;
    }
}
