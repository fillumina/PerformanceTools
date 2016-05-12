package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import static com.fillumina.performance.template.AutoParametrizedSequencePerformanceTemplate.testName;
import com.fillumina.performance.template.TestConfigurator;

/**
 *
 * @author Francesco Illuminati
 */
public class TestNgParametrizedSequencePerformanceTemplateTest
        extends TestNgParametrizedSequencePerformanceTemplate<Integer, Character> {

    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer LOOP_1 = 1_000;
    private static final Integer LOOP_2 = 2_000;
    private static final Integer LOOP_3 = 3_000;

    private static final String TEST = "test";


    public static void main(final String[] args) {
        new TestNgParametrizedSequencePerformanceTemplateTest()
                .executeWithOutput();
    }

    @Override
    public void config(TestConfigurator config) {
    }

    @Override
    public void addParameters(final ParameterContainer<Integer> parameters) {
        parameters.addParameter(NAME_1, LOOP_1)
                .addParameter(NAME_2, LOOP_2)
                .addParameter(NAME_3, LOOP_3);
    }

    @Override
    public void addSequence(final SequenceContainer<Character> sequences) {
        sequences.setSequence('x', 'y', 'z');
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
        for (char c: new char[] {'x', 'y', 'z'}) {
            assertion.forExecution(testName(TEST, c))
                    .assertPercentage(NAME_1).sameAs(33);

            assertion.forExecution(testName(TEST, c))
                    .assertPercentage(NAME_2).sameAs(66);

            assertion.forExecution(testName(TEST, c))
                    .assertPercentage(NAME_3).sameAs(100);
        }
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<Integer, Character>> tests) {
        tests.addTest(TEST, new ParametrizedSequenceTestable<Integer, Character>() {

            @Override
            public Object test(Integer param, Character sequence) {
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
