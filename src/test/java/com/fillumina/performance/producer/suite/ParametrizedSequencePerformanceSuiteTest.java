package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.CountingMap;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class ParametrizedSequencePerformanceSuiteTest {

    @Test
    public void shouldRunTheSameTestWithDifferentObjectAndSequenceItem() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        final ParametrizedSequencePerformanceSuite<Character, Integer> suite =
                new ParametrizedSequencePerformanceSuite<>(pt);

        suite.addObjectToTest("First Object", 'a');
        suite.addObjectToTest("Second Object", 'b');

        suite.addSequence(1, 2, 3);

        final CountingMap<String> countingMap = new CountingMap<>();

        suite.executeTest(10, new ParametrizedSequenceRunnable<Character, Integer>() {

            @Override
            public void call(final Character param, final Integer sequence) {
                final String key = String.valueOf(param) + sequence;
                countingMap.increment(key);
            }
        });

        assertEquals(6, countingMap.size());
        assertEquals(10, countingMap.getCounterFor("a1"), 0);
        assertEquals(10, countingMap.getCounterFor("a2"), 0);
        assertEquals(10, countingMap.getCounterFor("a3"), 0);
        assertEquals(10, countingMap.getCounterFor("b1"), 0);
        assertEquals(10, countingMap.getCounterFor("b2"), 0);
        assertEquals(10, countingMap.getCounterFor("b3"), 0);
    }
}
