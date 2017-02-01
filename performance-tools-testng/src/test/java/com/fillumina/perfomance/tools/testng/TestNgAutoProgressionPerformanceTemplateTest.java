package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati
 */
public class TestNgAutoProgressionPerformanceTemplateTest
        extends TestNgAutoProgressionPerformanceTemplate {

    @Override
    public void config(final TestConfiguration config) {
        config.speedTestOnly();
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("test", new AbstractTestable() {

            @Override
            public Object test() {
                return null;
            }
        });
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(10)
                .assertPercentage("test").sameAs(100);
    }
}
