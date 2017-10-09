package com.fillumina.performance.mem.stats;

import com.fillumina.performance.executor.test.SafeSink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.executor.generator.TestConfiguration;
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
    public void config(MixedConfigurationBuilder<?> configuration) {
        configuration.usedMemConfig();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertion) {
        assertion.usedMemory().tolerance(Ratio.percentage(10))
                .assertOrder("ArrayList").lessThan("LinkedList");
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("ArrayList", () -> { SafeSink.drain(new ArrayList<>()); });
        tests.addTest("LinkedList", () -> { SafeSink.drain(new LinkedList<>()); });
    }
}
