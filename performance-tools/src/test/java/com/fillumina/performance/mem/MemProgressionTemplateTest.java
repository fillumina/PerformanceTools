package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemProgressionTemplateTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new MemProgressionTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new MemProgressionTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.usedMemTestOnly();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
        assertion.usedMemoryWithTolerance(Ratio.percentage(10))
                .assertOrder("ArrayList").lessThan("LinkedList");
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("ArrayList", new Testable() {
            @Override
            public void test() {
                Sink.drain(new ArrayList<>());
            }
        });
        tests.addTest("LinkedList", new Testable() {
            @Override
            public void test() {
                Sink.drain(new LinkedList<>());
            }
        });
    }
}
