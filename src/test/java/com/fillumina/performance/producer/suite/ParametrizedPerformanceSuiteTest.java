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
public class ParametrizedPerformanceSuiteTest {

    @Test
    public void shouldRunTheSameTestOverDifferentObjects() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        final ParametrizedPerformanceSuite<String> suite =
                new ParametrizedPerformanceSuite<>(pt);

        suite.addObjectToTest("First Object", "one");
        suite.addObjectToTest("Second Object", "two");
        suite.addObjectToTest("Third Object", "three");

        final Map<String, Integer> map = new LinkedHashMap<>();

        suite.executeTest("First Test", 10, new ParametrizedRunnable<String>() {

            @Override
            public void call(final String param) {
                Integer counter = map.get(param);
                if (counter == null) {
                    counter = 0;
                }
                map.put(param, counter + 1);
            }
        });

        assertEquals(3, map.size());
        assertEquals(10, map.get("one"), 0);
        assertEquals(10, map.get("two"), 0);
        assertEquals(10, map.get("three"), 0);
    }
}
