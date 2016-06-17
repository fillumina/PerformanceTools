package com.fillumina.performance.util.junit;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.PerformanceStats;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedSequenceTestable;
import com.fillumina.performance.suite.SequenceContainer;
import com.fillumina.performance.template.TestConfigurator;

/**
 *
 * @author Francesco Illuminati
 */
public class JUnitParametrizedSequencePerformanceTemplateTest
        extends JUnitParametrizedSequencePerformanceTemplate<Integer, Character> {

    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer LOOP_1 = 1_000;
    private static final Integer LOOP_2 = 2_000;
    private static final Integer LOOP_3 = 3_000;

    private static final String TEST = "test";

    public static void main(final String[] args) {
        new JUnitParametrizedSequencePerformanceTemplateTest()
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
    public void addSequence(SequenceContainer<Character> sequences) {
        sequences.setSequence('x', 'y', 'z');
    }

    @Override
    public void addAssertions(
            AssertParametrizedSequencePerformance<Void, PerformanceStats> assertion) {
        for (char c: new char[] {'x', 'y', 'z'}) {
            assertion.forSequence(""+c).forAllTests(
                    AssertSpeed.withTolerance(5)
                    .assertPercentage(NAME_1).sameAs(33)
                    .assertPercentage(NAME_2).sameAs(66)
                    .assertPercentage(NAME_3).sameAs(100));
        }
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedSequenceTestable<Integer, Character>> tests) {
        tests.addTest("test", new ParametrizedSequenceTestable<Integer, Character>() {

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
