package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.suite.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.util.PerformanceTimeHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AutoParametrizedSequencePerformanceTemplateTest
        extends AutoParametrizedSequencePerformanceTemplate<String, Integer>{

    public static void main(final String[] args) {
        new AutoParametrizedSequencePerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setName("AutoParametrizedSequencePerformanceTemplateTest")
                .setMinConfidence(0.4)
                .setSamplesPerStep(33)
                .setBaseIterations(10)
                .setMaxPercentageMargin(5)
                .setTimeout(5, TimeUnit.MINUTES);
    }

    @Override
    public void addParameters(ParameterContainer<String> parameters) {
        parameters
                .addParameter("first", "first")
                .addParameter("second", "second");
    }

    @Override
    public void addSequence(
            SequenceContainer<Integer> sequences) {
        sequences.setSequence(1, 2);
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<String, Integer>> tests) {
        tests.addTest("test", new ParametrizedSequenceTestable<String, Integer>() {

            @Override
            public Object test(String param, Integer sequence) {
                PerformanceTimeHelper.sleepMicroseconds(5 * sequence);
                return null;
            }
        });
    }

    @Override
    public void addAssertions(AssertParametrizedSequencePerformance<?> assertion) {
        assertion.forAllSequences()
                .forAllTests(AssertPerformance.withTolerance(5)
                    .assertSpeed("first").sameAs("second"));
    }
}
