package com.fillumina.performance.producer.suite;

import com.fillumina.performance.util.Bag;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedPerformanceSuiteTest {
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final int SAMPLES = 2;
    public static final int ITERATIONS = 7;
    public static final int FIRST_ITERATION = 5;
    public static final int SECOND_ITERATION = 11;

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedPerformanceSuiteTest ppst =
                new ParametrizedPerformanceSuiteTest();
        ppst.printout = true;
        ppst.shouldRunTheSameTestOverDifferentObjects();
        ppst.shouldUseTheProgression();
    }

    @Test
    public void shouldRunTheSameTestOverDifferentObjects() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerBuilder.createSingleThreaded()
                .setIterations(ITERATIONS)

                .instrumentedBy(new ParametrizedPerformanceSuite<>())
                    .addParameter("First Object", ONE)
                    .addParameter("Second Object", TWO)
                    .addParameter("Third Object", THREE)

                .executeTest("SIMPLE", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        countingMap.add(param);
                    }
                }).whenever(printout).use(StringTableViewer.CONSUMER);

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS, countingMap.getCount(ONE));
        assertEquals(ITERATIONS, countingMap.getCount(TWO));
        assertEquals(ITERATIONS, countingMap.getCount(THREE));
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerBuilder.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                    .setSamplesPerMagnitude(SAMPLES)
                    .build())

                .instrumentedBy(new ParametrizedPerformanceSuite<>()
                    .addParameter("First Object", ONE)
                    .addParameter("Second Object", TWO)
                    .addParameter("Third Object", THREE))

                .executeTest("PROGRESSION", new ParametrizedRunnable<String>() {

                    @Override
                    public void call(final String param) {
                        bag.add(param);
                    }
                }).whenever(printout).use(StringTableViewer.CONSUMER);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(times, bag.getCount(ONE));
        assertEquals(times, bag.getCount(TWO));
        assertEquals(times, bag.getCount(THREE));
    }
}
