package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.AutoParametrizedSequencePerformanceTemplate;
import com.fillumina.performance.template.ParametrizedSequenceAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedSequencePerformanceTemplateTest
        extends AutoParametrizedSequencePerformanceTemplate<Double, Integer>{

    public static void main(final String[] args) {
        new AutoParametrizedSequencePerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
                .setName("AutoParametrizedSequencePerformanceTemplateTest")
                .speedTest()
                    .setMinConfidence(0.4)
                    .setMaxPercentageMargin(5)
                    .setTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public void addParameters(ParameterContainer<Double> parameters) {
        parameters
                .addParameter("half", 0.5)
                .addParameter("unit", 1.0);
    }

    @Override
    public void addSequence(
            SequenceContainer<Integer> sequences) {
        sequences.setSequence(1, 2);
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<Double, Integer>> tests) {
        tests.addTest("test", new ParametrizedSequenceTestable<Double, Integer>() {

            @Override
            public Object test(Double param, Integer sequence) {
                PerformanceTimeHelper.sleepMicroseconds(
                        (int)(50 * param * sequence));
                return null;
            }
        });
    }

    @Override
    public void addAssertions(ParametrizedSequenceAssertion assertion) {
        assertion.speed()
            .forAllSequences()
                .forAllTests(AssertSpeed.withTolerancePercentage(5)
                    .assertOrder("half").lessThan("unit"));
    }
}
