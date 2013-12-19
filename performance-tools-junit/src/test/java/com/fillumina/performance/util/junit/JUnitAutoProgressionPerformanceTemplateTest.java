package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.TestContainer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JUnitAutoProgressionPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    @Override
    public void init(final ProgressionConfigurator config) {
        config.setBaseIterations(1);
        config.setMaxStandardDeviation(10);
        config.setTimeoutSeconds(1);
    }

    @Override
    public void addTests(final TestContainer tests) {
        tests.addTest("test", new Runnable() {

            @Override
            public void run() {
                // do nothing
            }
        });
    }

    @Override
    public void addAssertions(final PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(1)
                .assertPercentageFor("test").sameAs(100);
    }
}
