package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.suite.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.util.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedPerformanceTemplateTest
        extends AutoParametrizedPerformanceTemplate<Integer> {

    public static void main(final String[] args) {
        new AutoParametrizedPerformanceTemplateTest()
                .executeWithIntermediateOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setSamplesPerStep(60)
                .setMinConfidence(0.7)
                .setMaxPercentageMargin(0.05)
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
    public void addAssertions(AssertParametrizedPerformance<?> assertion) {
        assertion.forAllTests(AssertPerformance.withTolerance(5)
                    .assertSpeed("first").sameAs("second"));
    }
}
