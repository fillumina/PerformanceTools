package com.fillumina.performance.util.junit;

import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.template.TestConfigurator;

/**
 *
 * @author Francesco Illuminati
 */
public class JUnitAutoProgressionPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    @Override
    public void config(final TestConfigurator config) {
        config.setBaseIterations(1);
        config.setMaxPercentageMargin(0.1);
        config.setTimeoutSeconds(1);
    }

    @Override
    public void addTests(final TestContainer<Testable> tests) {
        tests.addTest("test", new AbstractTestable() {

            @Override
            public Object test() {
                return null;
            }
        });
    }

    @Override
    public void addAssertions(final PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(1)
                .assertPercentage("test").sameAs(100);
    }
}
