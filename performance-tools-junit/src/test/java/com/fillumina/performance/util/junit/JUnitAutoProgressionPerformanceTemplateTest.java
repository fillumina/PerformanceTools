package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;

/**
 *
 * @author fra
 */
public class JUnitAutoProgressionPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

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
    protected void addAssertions(final AssertPerformance ap) {
        ap.setTolerancePercentage(1)
                .assertPercentageFor("test").equals(100);
    }

}
