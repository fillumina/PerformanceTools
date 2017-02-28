package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParameterizedTemplateTest
            extends ParameterizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new MemParameterizedTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new MemParameterizedTemplateTest().executeWithoutOutput();
    }

    @Override
    public void addParameters(ParameterContainer<Integer> parameters) {
        parameters
                .addParameter("0", 0)
                .addParameter("7", 7)
                .addParameter("10", 10)
                .addParameter("2_000", 2_000);
    }

    @Override
    public void addAssertions(ParameterizedMixedAssertion assertion) {
        assertion.usedMem()
                .forTest("test")
                    .setTolerance(Ratio.percentage(10))
                        .assertOrder("0").lessThan("2_000");
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .usedMemTestOnly();
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest("test", new ParameterizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                return new byte[param];
            }
        });
    }
}
