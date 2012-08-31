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
public class ParametrizedPerformanceSuiteTest {

    @Test
    public void shouldRunTheSameTestOverDifferentObjects() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        final ParametrizedPerformanceSuite<String> suite =
                new ParametrizedPerformanceSuite<>(pt);

        suite.addObjectToTest("First Object", "one");
        suite.addObjectToTest("Second Object", "two");
        suite.addObjectToTest("Third Object", "three");

        final CountingMap<String> countingMap = new CountingMap<>();

        suite.executeTest("First Test", 10, new ParametrizedRunnable<String>() {

            @Override
            public void call(final String param) {
                countingMap.increment(param);
            }
        });

        assertEquals(3, countingMap.size());
        assertEquals(10, countingMap.getCounterFor("one"), 0);
        assertEquals(10, countingMap.getCounterFor("two"), 0);
        assertEquals(10, countingMap.getCounterFor("three"), 0);
    }
}
