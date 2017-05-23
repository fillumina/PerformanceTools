package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
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
    public void config(Configuration configuration) {
        configuration.usedMemTestOnly();
    }

    @Override
    public void addAssertions(MixedAssertion assertion) {
        assertion.usedMemoryWithTolerance(Ratio.percentage(10))
                .assertOrder("ArrayList").lessThan("LinkedList");
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("ArrayList", () -> { Sink.drain(new ArrayList<>()); });
        tests.addTest("LinkedList", () -> { Sink.drain(new LinkedList<>()); });
    }
}
