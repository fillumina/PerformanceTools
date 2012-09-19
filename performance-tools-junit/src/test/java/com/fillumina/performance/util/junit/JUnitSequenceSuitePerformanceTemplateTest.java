package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequencePerformanceSuite;
import com.fillumina.performance.producer.suite.ParametrizedSequenceRunnable;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class JUnitSequenceSuitePerformanceTemplateTest
        extends JUnitSequenceSuitePerformanceTemplate<Integer, Character> {

    private static final String NAME_1 = "OBJ1";
    private static final String NAME_2 = "OBJ2";
    private static final String NAME_3 = "OBJ3";

    private static final Integer SLEEP_1 = 10;
    private static final Integer SLEEP_2 = 20;
    private static final Integer SLEEP_3 = 30;

    private static final String TEST = "test";

    private Map<String, Integer> mapCounter = new HashMap<>();

    public static void main(final String[] args) {
        new JUnitSequenceSuitePerformanceTemplateTest().testWithOutput();
    }

    @Override
    public void init(PerformanceInstrumenterBuilder config) {
        config.setBaseIterations(1)
                .setMaxStandardDeviation(10);
    }

    @Override
    public void addObjects(
            ParametrizedSequencePerformanceSuite<Integer, Character> suite) {
        suite
                .addObjectToTest(NAME_1, SLEEP_1)
                .addObjectToTest(NAME_2, SLEEP_2)
                .addObjectToTest(NAME_3, SLEEP_3);
    }

    @Override
    public void addSequence(
            ParametrizedSequencePerformanceSuite<Integer, Character> suite) {
        suite.addSequence('x', 'y', 'z');
    }

    @Override
    public void addAssertions(AssertPerformanceForExecutionSuite ap) {
        for (char c: new char[] {'x', 'y', 'z'}) {
            ap.forExecution(createTestName(TEST, c))
                    .assertPercentageFor(NAME_1).sameAs(33);

            ap.forExecution(createTestName(TEST, c))
                    .assertPercentageFor(NAME_2).sameAs(66);

            ap.forExecution(createTestName(TEST, c))
                    .assertPercentageFor(NAME_3).sameAs(100);
        }
    }

    @Override
    public void executeTests(
            ParametrizedSequencePerformanceSuite<Integer, Character> suite) {

        suite.executeTest(TEST,
                new ParametrizedSequenceRunnable<Integer, Character>() {

            @Override
            public void call(Integer param, Character sequence) {
                count(param, sequence);
                try {
                    Thread.sleep(param);
                } catch (InterruptedException ex) {
                }
            }
        });
    }

    @Override
    public void onAfterExecution(
            ParametrizedSequencePerformanceSuite<Integer, Character> suite) {
        for (char c: new char[] {'x', 'y', 'z'}) {
            assertEquals(10, getCount(SLEEP_1, c));
            assertEquals(10, getCount(SLEEP_2, c));
            assertEquals(10, getCount(SLEEP_3, c));
        }
    }

    private void count(int param, char sequence) {
        final String name = createTestName("" + param, sequence);
        Integer value = mapCounter.get(name);
        if (value == null) {
            value = 0;
        }
        mapCounter.put(name, value + 1);
    }

    private int getCount(int param, char sequence) {
        Integer value = mapCounter.get(createTestName("" + param, sequence));
        return value != null ? value : 0;
    }
}
