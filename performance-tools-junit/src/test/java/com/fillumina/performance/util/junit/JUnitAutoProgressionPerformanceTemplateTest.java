package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;

/**
 *
 * @author fra
 */
public class JUnitAutoProgressionPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    @Override
    public void init(final PerformanceInstrumenterBuilder builder) {
        builder.setBaseIterations(1);
        builder.setMaxStandardDeviation(10);
        builder.setTimeoutSeconds(1);
    }

    @Override
    public void addTests(InstrumentablePerformanceExecutor<?> pe) {
        pe.addTest("test", new Runnable() {

            @Override
            public void run() {
                // do nothing
            }
        });
    }

    @Override
    public void addAssertions(final AssertPerformance ap) {
        ap.setTolerancePercentage(1)
                .assertPercentageFor("test").equals(100);
    }
}
