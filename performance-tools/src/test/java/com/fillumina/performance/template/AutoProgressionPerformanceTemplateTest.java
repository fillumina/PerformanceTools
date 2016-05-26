package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.Testable;
import com.fillumina.performance.util.PerformanceTimeHelper;
import org.junit.Test;
import com.fillumina.performance.stats.assertion.PerformanceStatsAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new AutoProgressionPerformanceTemplateTest()
                .executeWithIntermediateOutput();
    }

    @Test
    public void shouldExecuteTheTest() {
        new AutoProgressionPerformanceTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setName("AutoProgressionPerformanceTemplateTest")
                .setSamplesPerStep(30);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("half", new AbstractTestable() {

            @Override
            public Object test() {
                PerformanceTimeHelper.sleepMicroseconds(10);
                return null;
            }
        });

        tests.addTest("full", new AbstractTestable() {

            @Override
            public Object test() {
                PerformanceTimeHelper.sleepMicroseconds(20);
                return null;
            }
        });
    }

    @Override
    public void addAssertions(PerformanceStatsAssertion assertion) {
        assertion.assertSpeed("half").fasterThan("full");
    }
}
