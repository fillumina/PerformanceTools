package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.template.TestConfigurator;

/**
 *
 * @author Francesco Illuminati
 */
public class TestNgAutoProgressionPerformanceTemplateTest
        extends TestNgAutoProgressionPerformanceTemplate {

    @Override
    public void config(final TestConfigurator config) {
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
    public void addAssertions(final PerformanceAssertion assertion) {
        assertion.assertPercentage("test").sameAs(100);
    }
}
