package com.fillumina.performance.template;

import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.suite.ParameterContainer;
import com.fillumina.performance.sample.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.sample.suite.SequenceContainer;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.util.PerformanceTimeHelper;
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
    public void testSomeMethod() {
    }

    @Override
    public void config(TestConfigurator configuration) {
    }

    @Override
    public void addParameters(ParameterContainer<String> parameters) {
        parameters.addParameter("first", "first")
                .addParameter("second", "second");
    }

    @Override
    public void addSequence(
            SequenceContainer<?, Integer> sequences) {
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
    public void addAssertions(PerformanceAssertion assertion) {
        assertion.assertTest("test_1_first").sameAs("test_1_second");
        assertion.assertTest("test_2_first").sameAs("test_2_second");
    }

}
