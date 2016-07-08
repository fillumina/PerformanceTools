package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.template.AutoProgressionPerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemProgressionTemplateTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new MemProgressionTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new MemProgressionTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.performUsedMemTest();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.usedMemoryWithTolerance(10)
                .assertOrder("ArrayList").lessThan("LinkedList");
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("ArrayList", new AbstractTestable() {
            @Override
            public Object test() {
                return new ArrayList<>();
            }
        });
        tests.addTest("LinkedList", new AbstractTestable() {
            @Override
            public Object test() {
                return new LinkedList<>();
            }
        });
    }
}
