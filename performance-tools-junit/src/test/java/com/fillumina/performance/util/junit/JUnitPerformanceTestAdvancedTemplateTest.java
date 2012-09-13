package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;

/**
 *
 * @author fra
 */
public class JUnitPerformanceTestAdvancedTemplateTest
        extends JUnitPerformanceTestAdvancedTemplate {

    @Override
    protected void init() {
        setBaseIterations(1);
        setMaxStandardDeviation(10);
        setTimeoutSeconds(1);
    }

    @Override
    protected void addTests(InstrumentablePerformanceExecutor<?> pe) {
        pe.addTest("test", new Runnable() {

            @Override
            public void run() {
                // do nothing
            }
        });
    }

    @Override
    protected PerformanceConsumer createAssertions() {
        return AssertPerformance.withTolerance(1)
                .assertPercentageFor("test").equals(100);
    }

}
