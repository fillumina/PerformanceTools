package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.template.ProgressionConfigurator;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import com.fillumina.performance.producer.suite.SequenceContainer;
import com.fillumina.performance.template.AssertionSuiteBuilder;
import static com.fillumina.performance.template.ParametrizedSequencePerformanceTemplate.testName;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JUnitParametrizedSequencePerformanceTemplateTest
        extends JUnitParametrizedSequencePerformanceTemplate<Integer, Character> {

    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer SLEEP_1 = 10;
    private static final Integer SLEEP_2 = 20;
    private static final Integer SLEEP_3 = 30;

    private static final String TEST = "test";

    private Map<String, Integer> mapCounter = new HashMap<>();

    public static void main(final String[] args) {
        new JUnitParametrizedSequencePerformanceTemplateTest().executeWithOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        config.setBaseIterations(1)
                .setMaxStandardDeviation(10);
    }

    @Override
    public void addParameters(final ParametersContainer<Integer> parameters) {
        parameters.addParameter(NAME_1, SLEEP_1)
                .addParameter(NAME_2, SLEEP_2)
                .addParameter(NAME_3, SLEEP_3);
    }

    @Override
    public void addSequence(final SequenceContainer<?, Character> sequences) {
        sequences.setSequence('x', 'y', 'z');
    }

    @Override
    public void addAssertions(final AssertionSuiteBuilder assertionBuilder) {
        final SuiteExecutionAssertion assertion =
                assertionBuilder.withTolerance(7);
        for (char c: new char[] {'x', 'y', 'z'}) {
            assertion.forExecution(testName(TEST, c))
                    .assertPercentageFor(NAME_1).sameAs(33);

            assertion.forExecution(testName(TEST, c))
                    .assertPercentageFor(NAME_2).sameAs(66);

            assertion.forExecution(testName(TEST, c))
                    .assertPercentageFor(NAME_3).sameAs(100);
        }
    }

    @Override
    public void addIntermediateAssertions(final PerformanceAssertion assertion) {
        assertion.setPercentageTolerance(7)
                .assertPercentageFor(NAME_1).sameAs(33)
                .assertPercentageFor(NAME_2).sameAs(66)
                .assertPercentageFor(NAME_3).sameAs(100);
    }

    @Override
    public ParametrizedSequenceRunnable<Integer, Character> getTest() {
        return new ParametrizedSequenceRunnable<Integer, Character>() {

            @Override
            public Object sink(Integer param, Character sequence) {
                count(param, sequence);
                try {
                    Thread.sleep(param);
                } catch (InterruptedException ex) {
                }
                return null;
            }
        };
    }

    @Override
    public void onAfterExecution(
            final Map<String, LoopPerformances> performancesMap) {
        for (char c: new char[] {'x', 'y', 'z'}) {
            assertEquals(10, getCount(SLEEP_1, c));
            assertEquals(10, getCount(SLEEP_2, c));
            assertEquals(10, getCount(SLEEP_3, c));
        }
    }

    private void count(int param, char sequence) {
        final String name = testName("" + param, sequence);
        Integer value = mapCounter.get(name);
        if (value == null) {
            value = 0;
        }
        mapCounter.put(name, value + 1);
    }

    private int getCount(int param, char sequence) {
        Integer value = mapCounter.get(testName("" + param, sequence));
        return value != null ? value : 0;
    }
}
