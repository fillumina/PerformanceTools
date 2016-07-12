package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoProgressionPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new AutoProgressionPerformanceTemplateTest()
                .executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        new AutoProgressionPerformanceTemplateTest()
                .executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .setName("AutoProgressionPerformanceTemplateTest")
                .performSpeedTest()
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
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.speedWithTolerance(10)
                .assertOrder("half").lessThan("full");
    }
}
