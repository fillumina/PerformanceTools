package com.fillumina.performance.util.junit;

import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.stats.assertion.PerformanceStatsAssertion;

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
    public void addAssertions(final PerformanceStatsAssertion assertion) {
        assertion.withPercentageTolerance(1)
                .assertPercentage("test").sameAs(100);
    }
}
