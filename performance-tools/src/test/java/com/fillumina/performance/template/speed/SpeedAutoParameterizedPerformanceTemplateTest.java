package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.ParameterizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedAutoParameterizedPerformanceTemplateTest
        extends ParameterizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new SpeedAutoParameterizedPerformanceTemplateTest()
                .executeWithMediumOutput();
//                .executeWithFullOutput();
//                .executeReportingOnlyResults();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.setName("AutoParameterizedPerformanceTemplateTest")
                .speedTestOnly()
                    .setSamples(100)
                    .setBaseIterations(10)
                    .setMaxPercentageMargin(5)
                    .setTimeout(5, TimeUnit.MINUTES);
    }

    @Override
    public void addParameters(ParameterContainer<Integer> parameters) {
        parameters.addParameter("one", 1)
                .addParameter("two", 2)
                .addParameter("three", 3);
    }

    @Override
    public void addTests(TestContainer<ParameterizedTestable<Integer>> tests) {
        tests.addTest("single", new ParameterizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                PerformanceTimeHelper.sleepMicroseconds(5 * param);
                return null;
            }
        });

        tests.addTest("double", new ParameterizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                PerformanceTimeHelper.sleepMicroseconds(10 * param);
                return null;
            }
        });
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
        assertion.speed()
            .forAllTests()
                .withTolerance(5)
                    .assertOrder("one").lessThan("three")
                .end()
            .forTest("single")
                .withTolerance(5)
                    .assertPercentage("three").sameAs(100)
                    .assertPercentage("one").sameAs(33)
                .end()
            .endTests();

    }
}
