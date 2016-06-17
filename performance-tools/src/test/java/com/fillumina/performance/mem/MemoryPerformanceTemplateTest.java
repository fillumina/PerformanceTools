package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.LfsrTest;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.template.AutoProgressionPerformanceTemplate;
import com.fillumina.performance.template.TestConfigurator;
import org.junit.Test;
import com.fillumina.performance.assertion.StatsAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new MemoryPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(StatsAssertion assertion) {
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration.setTimeoutSeconds(120);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("memoryHog", new AbstractTestable() {
            @Override
            public Object test() {
                return new int[1_000];
            }
        });
        tests.addTest("memoryEmpty", LfsrTest.INSTANCE);
    }
}
