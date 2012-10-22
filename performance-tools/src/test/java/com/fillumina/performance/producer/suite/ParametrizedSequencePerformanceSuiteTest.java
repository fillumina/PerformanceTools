package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.CountingMap;
import com.fillumina.performance.PerformanceTimerBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequencePerformanceSuiteTest {
    private static final int ITERATIONS = 10;

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final CountingMap<String> countingMap = new CountingMap<>();

        PerformanceTimerBuilder.createSingleThread()
            .setIterations(ITERATIONS)

            .instrumentedBy(new ParametrizedSequencePerformanceSuite<Character, Integer>())

            .addObjectToTest("First Object", 'a')
            .addObjectToTest("Second Object", 'b')

            .addSequence(1, 2, 3)


            .executeTest("count", new ParametrizedSequenceRunnable<Character, Integer>() {

                @Override
                public void call(final Character param, final Integer sequence) {
                    final String key = String.valueOf(param) + sequence;
                    countingMap.increment(key);
                }
            });

            assertEquals(6, countingMap.size());
            assertEquals(ITERATIONS, countingMap.getCounterFor("a1"), 0);
            assertEquals(ITERATIONS, countingMap.getCounterFor("a2"), 0);
            assertEquals(ITERATIONS, countingMap.getCounterFor("a3"), 0);
            assertEquals(ITERATIONS, countingMap.getCounterFor("b1"), 0);
            assertEquals(ITERATIONS, countingMap.getCounterFor("b2"), 0);
            assertEquals(ITERATIONS, countingMap.getCounterFor("b3"), 0);
    }
}
