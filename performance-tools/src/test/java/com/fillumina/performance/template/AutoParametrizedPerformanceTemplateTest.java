package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.util.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedPerformanceTemplateTest
        extends AutoParametrizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new AutoParametrizedPerformanceTemplateTest()
                .executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration.setName("AutoParametrizedPerformanceTemplateTest")
                .speed()
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
            .forAllTests(AssertSpeed.withTolerancePercentage(5)
                .assertOrder("one").lessThan("three"))
            .forTest("single", AssertSpeed.withTolerancePercentage(5)
                .assertPercentage("three").sameAs(100)
                .assertPercentage("one").sameAs(33));
    }
}
