package com.fillumina.performance.producer.suite;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.LinkedHashMap;
import java.util.Map;
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

        final Map<String, Integer> map = new LinkedHashMap<>();

        suite.executeTest(10, new ParametrizedSequenceRunnable<Character, Integer>() {

            @Override
            public void call(final Character param, final Integer sequence) {
                final String key = String.valueOf(param) + sequence;
                Integer counter = map.get(key);
                if (counter == null) {
                    counter = 0;
                }
                map.put(key, counter + 1);
            }
        });

        assertEquals(6, map.size());
        assertEquals(10, map.get("a1"), 0);
        assertEquals(10, map.get("a2"), 0);
        assertEquals(10, map.get("a3"), 0);
        assertEquals(10, map.get("b1"), 0);
        assertEquals(10, map.get("b2"), 0);
        assertEquals(10, map.get("b3"), 0);
    }
}
