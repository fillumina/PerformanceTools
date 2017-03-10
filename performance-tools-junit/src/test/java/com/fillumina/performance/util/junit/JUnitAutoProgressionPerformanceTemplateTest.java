package com.fillumina.performance.util.junit;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.AbstractTestable;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati
 */
public class JUnitAutoProgressionPerformanceTemplateTest
        extends JUnitAutoProgressionPerformanceTemplate {

    @Override
    public void config(final TestConfiguration config) {
        config.speedTestOnly()
            .setBaseIterations(1)
            .setMaxPercentageMargin(5)
            .setTimeoutSeconds(1);
    }

    @Override
    public void addTests(final TestContainer<Testable> tests) {
        tests.addTest("test", new AbstractTestable() {

            @Override
            public void test() {
            }
        });
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(Ratio.percentage(1))
                .assertPercentage("test").sameAs(100);
    }
}
