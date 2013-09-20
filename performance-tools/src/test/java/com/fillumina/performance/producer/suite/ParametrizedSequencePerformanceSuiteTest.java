package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.Bag;
import com.fillumina.performance.PerformanceTimerFactory;
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

        PerformanceTimerFactory.createSingleThreaded()
            .setIterations(ITERATIONS)

            .instrumentedBy(
                new ParametrizedSequencePerformanceSuite<Character, Integer>()
                .addParameter("First Object", 'a')
                .addParameter("Second Object", 'b')
                .setSequence(1, 2, 3))

            .addPerformanceConsumer(printout ? StringTableViewer.INSTANCE : null)

            .executeTest(new ParametrizedSequenceRunnable<Character, Integer>() {

                @Override
                public void call(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.add(key);
                }
            }).whenever(printout).use(StringTableViewer.INSTANCE);

            assertEquals(6, countingMap.size());
            assertEquals(ITERATIONS, countingMap.getCount("a1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("a3"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b1"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b2"), 0);
            assertEquals(ITERATIONS, countingMap.getCount("b3"), 0);
    }
}
