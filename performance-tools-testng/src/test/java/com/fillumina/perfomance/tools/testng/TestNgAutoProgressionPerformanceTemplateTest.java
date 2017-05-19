package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati
 */
public class TestNgAutoProgressionPerformanceTemplateTest
        extends TestNgAutoProgressionPerformanceTemplate {

    @Override
    public void config(final Configuration config) {
        config.speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("test", new Testable() {

            @Override
            public void run() {
            }
        });
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(Ratio.percentage(10))
                .assertPercentage("test").sameAs(100);
    }
}
