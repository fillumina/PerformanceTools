package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class SuiteTemplate {

    public abstract void init();

    public abstract void addObjects(final ParametrizedPerformanceSuite<Map<Integer, String>> suite);

    public abstract void addAssertions(final AssertPerformanceForExecutionSuite ap);

    public abstract void executeTests(final ParametrizedPerformanceSuite<Map<Integer, String>> suite);

    public ParametrizedPerformanceSuite<Map<Integer, String>> executeSuite(final PerformanceConsumer resultConsumer) {
        init();
        
        final ParametrizedPerformanceSuite<Map<Integer, String>> suite = createSingleThreadPerformanceSuite();
        addObjects(suite);
        suite.addPerformanceConsumer(resultConsumer);
        final AssertPerformanceForExecutionSuite ap = new AssertPerformanceForExecutionSuite();
        addAssertions(ap);
        suite.addPerformanceConsumer(ap);
        executeTests(suite);
        return suite;
    }

    protected ParametrizedPerformanceSuite<Map<Integer, String>> createSingleThreadPerformanceSuite() {
        final ParametrizedPerformanceSuite<Map<Integer, String>> suite = PerformanceTimerBuilder.createSingleThread().
                instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()).
                setSamplesPerMagnitude(15).setBaseIterations(1_000).
                setTimeout(60, TimeUnit.SECONDS).setMaxStandardDeviation(1.4).
                build().
                instrumentedBy(new ParametrizedPerformanceSuite<Map<Integer, String>>());
        return suite;
    }


}
