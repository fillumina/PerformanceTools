package com.fillumina.performance.util.junit;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.template.ParametrizedAssertion;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati
 */
public class JUnitParametrizedPerformanceTemplateTest
        extends JUnitParametrizedPerformanceTemplate<Integer> {
    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer LOOP_1 = 1_000;
    private static final Integer LOOP_2 = 2_000;
    private static final Integer LOOP_3 = 3_000;

    private static final String TEST = "test";

    public static void main(final String[] args) {
        new JUnitParametrizedPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Override
    public void config(final TestConfiguration config) {
    }

    @Override
    public void addParameters(final ParameterContainer<Integer> parameters) {
        parameters
                .addParameter(NAME_1, LOOP_1)
                .addParameter(NAME_2, LOOP_2)
                .addParameter(NAME_3, LOOP_3);
    }

    @Override
    public void addAssertions(ParametrizedAssertion assertion) {
        assertion.speed()
            .forTest(TEST,
                AssertSpeed.withTolerance(5)
                .assertPercentage(NAME_1).sameAs(33)
                .assertPercentage(NAME_2).sameAs(66)
                .assertPercentage(NAME_3).sameAs(100));

    }

    @Override
    public void addTests(TestContainer<ParametrizedTestable<Integer>> tests) {
        tests.addTest(TEST, new ParametrizedTestable<Integer>() {

            @Override
            public Object test(final Integer param) {
                return sum(param);
            }
        });
    }

    private int sum(int times) {
        int result = 0;
        for (int i=0; i<times; i++) {
            result += i;
        }
        return result;
    }
}
