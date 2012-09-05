package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.CountingMap;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class ParametrizedPerformanceSuiteTest {
    private static final int ITERATIONS = 10;

    @Test
    public void shouldRunTheSameTestOverDifferentObjects() {
        final CountingMap<String> countingMap = new CountingMap<>();

        PerformanceTimerBuilder.createSingleThread()
                .setIterations(ITERATIONS)

                .instrumentedBy(new ParametrizedPerformanceSuite<>())

                .addObjectToTest("First Object", "one")
                .addObjectToTest("Second Object", "two")
                .addObjectToTest("Third Object", "three")

                .executeTest("First Test", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        countingMap.increment(param);
                    }
                });

        assertEquals(3, countingMap.size());
        assertEquals(ITERATIONS, countingMap.getCounterFor("one"));
        assertEquals(ITERATIONS, countingMap.getCounterFor("two"));
        assertEquals(ITERATIONS, countingMap.getCounterFor("three"));
    }

    @Test
    public void shouldUseTheProgression() {
        final CountingMap<String> countingMap = new CountingMap<>();

        PerformanceTimerBuilder.createSingleThread()
                
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                    .setIterationProgression(10, 100)
                    .setSamplePerIterations(ITERATIONS)
                    .build()

                .instrumentedBy(new ParametrizedPerformanceSuite<>())

                .addObjectToTest("First Object", "one")
                .addObjectToTest("Second Object", "two")
                .addObjectToTest("Third Object", "three")

                .executeTest("First Test", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        countingMap.increment(param);
                    }
                });

        assertEquals(3, countingMap.size());
        assertEquals(110 * ITERATIONS, countingMap.getCounterFor("one"));
        assertEquals(110 * ITERATIONS, countingMap.getCounterFor("two"));
        assertEquals(110 * ITERATIONS, countingMap.getCounterFor("three"));
    }
}
