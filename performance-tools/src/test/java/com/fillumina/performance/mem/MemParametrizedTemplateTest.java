package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.template.AutoParametrizedPerformanceTemplate;
import com.fillumina.performance.template.ParametrizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemParametrizedTemplateTest
            extends AutoParametrizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new MemParametrizedTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new MemParametrizedTemplateTest().executeWithoutOutput();
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
    public void addAssertions(ParametrizedAssertion assertion) {
        assertion.memUsed()
                .forAllTests(AssertMemory.withTolerance(10)
                        .assertOrder("0").lessThan("2_000"));
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.usedMemTest();
    }

    @Override
    public void addTests(TestContainer<ParametrizedTestable<Integer>> tests) {
        tests.addTest("test", new ParametrizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                return new byte[param];
            }
        });
    }
}
