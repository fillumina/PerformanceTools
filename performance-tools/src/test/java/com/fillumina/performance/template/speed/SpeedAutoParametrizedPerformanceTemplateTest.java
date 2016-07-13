package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.template.AutoParametrizedPerformanceTemplate;
import com.fillumina.performance.template.ParametrizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedAutoParametrizedPerformanceTemplateTest
        extends AutoParametrizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new SpeedAutoParametrizedPerformanceTemplateTest()
                .executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.setName("AutoParametrizedPerformanceTemplateTest")
                .speedTest()
                    .setMinConfidence(0.7)
                    .setSamplesPerStep(33)
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
    public void addTests(TestContainer<ParametrizedTestable<Integer>> tests) {
        tests.addTest("single", new ParametrizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                PerformanceTimeHelper.sleepMicroseconds(5 * param);
                return null;
            }
        });

        tests.addTest("double", new ParametrizedTestable<Integer>() {
            @Override
            public Object test(Integer param) {
                PerformanceTimeHelper.sleepMicroseconds(10 * param);
                return null;
            }
        });
    }

    @Override
    public void addAssertions(ParametrizedAssertion assertion) {
        assertion.speed()
            .forAllTests(AssertSpeed.withTolerance(5)
                .assertOrder("one").lessThan("three"))
            .forTest("single", AssertSpeed.withTolerance(5)
                .assertPercentage("three").sameAs(100)
                .assertPercentage("one").sameAs(33));
    }
}
