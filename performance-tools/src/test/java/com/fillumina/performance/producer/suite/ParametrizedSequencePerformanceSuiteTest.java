package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.Bag;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedSequencePerformanceSuiteTest test =
                new ParametrizedSequencePerformanceSuiteTest();
        test.printout = true;
        test.shouldRunTheSameTestWithDifferentObjectAndSequenceItem();
    }

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerBuilder.createSingleThreaded()
            .setIterations(ITERATIONS)

            .instrumentedBy(new ParametrizedSequencePerformanceSuite<Character, Integer>()
                .addObjectToTest("First Object", 'a')
                .addObjectToTest("Second Object", 'b')
                .addSequence(1, 2, 3))

            .addPerformanceConsumer(printout ? StringTableViewer.CONSUMER : null)

            .executeTest(new ParametrizedSequenceRunnable<Character, Integer>() {

                @Override
                public void call(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                }
            }).whenever(printout).use(StringTableViewer.CONSUMER);

            assertEquals(6, countingMap.size());
            assertEquals(ITERATIONS, countingMap.getCount("a1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a3"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b3"), 0);
    }
}
