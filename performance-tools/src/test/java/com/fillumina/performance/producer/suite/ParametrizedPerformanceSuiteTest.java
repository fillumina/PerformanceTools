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
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final int SAMPLES = 2;
    public static final int ITERATIONS = 7;
    public static final int FIRST_ITERATION = 5;
    public static final int SECOND_ITERATION = 11;

    @Test
    public void shouldRunTheSameTestOverDifferentObjects() {
        final CountingMap<String> countingMap = new CountingMap<>();

        PerformanceTimerBuilder.createSingleThread()
                .setIterations(ITERATIONS)

                .instrumentedBy(new ParametrizedPerformanceSuite<>())

                .addObjectToTest("First Object", ONE)
                .addObjectToTest("Second Object", TWO)
                .addObjectToTest("Third Object", THREE)

                .executeTest("First Test", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        countingMap.increment(param);
                    }
                });

        assertEquals(3, countingMap.size());
        
        assertEquals(ITERATIONS, countingMap.getCounterFor(ONE));
        assertEquals(ITERATIONS, countingMap.getCounterFor(TWO));
        assertEquals(ITERATIONS, countingMap.getCounterFor(THREE));
    }

    @Test
    public void shouldUseTheProgression() {
        final CountingMap<String> countingMap = new CountingMap<>();

        PerformanceTimerBuilder.createSingleThread()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                    .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                    .setSamplesPerMagnitude(SAMPLES)
                    .build()

                .instrumentedBy(new ParametrizedPerformanceSuite<>())

                .addObjectToTest("First Object", ONE)
                .addObjectToTest("Second Object", TWO)
                .addObjectToTest("Third Object", THREE)

                .executeTest("First Test", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        countingMap.increment(param);
                    }
                });

        assertEquals(3, countingMap.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(times, countingMap.getCounterFor(ONE));
        assertEquals(times, countingMap.getCounterFor(TWO));
        assertEquals(times, countingMap.getCounterFor(THREE));
    }
}
