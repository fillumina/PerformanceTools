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
    private static final String FIRST = "first";
    private static final String SECOND = "second";

    public static void main(final String[] args) {
        new AutoParametrizedPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new AutoParametrizedPerformanceTemplateTest().executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTest().setTimeoutSeconds(120)
                .usedMemTest()
                .allocatedMemTest();
    }

    @Override
    public void addParameters(ParameterContainer<Integer> params) {
        params
                .addParameter("1", 1)
                .addParameter("2", 2);
    }

    @Override
    public void addTests(TestContainer<ParametrizedTestable<Integer>> tests) {
        tests.addTest(FIRST, new ParametrizedTestable<Integer>() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test(Integer param) {
                for (int i=0; i<param; i++) {
                    lfsr.next();
                }
                return new int[param];
            }
        });
        tests.addTest(SECOND, new ParametrizedTestable<Integer>() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test(Integer param) {
                lfsr.next();
                return new int[10];
            }
        });
    }

    @Override
    public void addAssertions(ParametrizedAssertion assertion) {
    }

}
