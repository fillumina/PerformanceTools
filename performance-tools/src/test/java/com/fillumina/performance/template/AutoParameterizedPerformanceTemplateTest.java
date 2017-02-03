package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParameterizedPerformanceTemplateTest
        extends AutoParameterizedPerformanceTemplate<Integer> {
    private static final String FIRST = "first";
    private static final String SECOND = "second";

    public static void main(final String[] args) {
        new AutoParameterizedPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration config) {
        config
                .speedTest()
                    .setTimeoutSeconds(340)
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
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest(FIRST, new ParameterizedTestable<Integer>() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test(Integer param) {
                for (int i=0; i<param; i++) {
                    lfsr.next();
                }
                return new int[param];
            }
        });
        tests.addTest(SECOND, new ParameterizedTestable<Integer>() {
            LinearFeedbackShiftRegister lfsr = new LinearFeedbackShiftRegister();

            @Override
            public Object test(Integer param) {
                lfsr.next();
                return new int[10];
            }
        });
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
        assertion
                .speed()
                    .forTest(FIRST)
                        .withTolerance(5)
                        .assertOrder("1").lessThan("2")
                    .end()
                .endTests()
                .usedMem()
                    .forTest(SECOND)
                        .withTolerance(5)
                        .assertValue("1").sameAs(56);
    }

}
