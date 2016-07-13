package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedPerformanceTemplateTest
        extends AutoParametrizedPerformanceTemplate<Integer> {
    private static final String TEST = "test";

    public static void main(final String[] args) {
        new AutoParametrizedPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new AutoParametrizedPerformanceTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
    }

    @Override
    public void addParameters(ParameterContainer<Integer> parameters) {
        parameters
                .addParameter("1", 1)
                .addParameter("2", 2);
    }

    @Override
    public void addTests(TestContainer<ParametrizedTestable<Integer>> tests) {
        tests.addTest(TEST, new ParametrizedTestable<Integer>() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test(Integer param) {
                for (int i=0; i<param; i++) {
                    lfsr.next();
                }
                return new int[param];
            }
        });
    }

    @Override
    public void addAssertions(ParametrizedAssertion assertion) {
    }

}
