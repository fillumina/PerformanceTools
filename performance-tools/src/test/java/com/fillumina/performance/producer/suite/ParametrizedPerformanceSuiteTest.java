package com.fillumina.performance.producer.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Bag;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ParametrizedPerformanceSuiteTest {
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    // they are primitives to allow unique results
    private static final int SAMPLES = 3;
    public static final int ITERATIONS = 7;
    public static final int FIRST_ITERATION = 5;
    public static final int SECOND_ITERATION = 11;

    private boolean printout = false;

    public static void main(final String[] args) {
        final ParametrizedPerformanceSuiteTest ppst =
                new ParametrizedPerformanceSuiteTest();
        ppst.printout = true;
        ppst.shouldRunTheSameTestOverDifferentParameters();
        ppst.shouldAssertOverDifferentParameters();
        ppst.shouldUseTheProgression();
    }

    @Test
    public void shouldRunTheSameTestOverDifferentParameters() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
                .setIterations(ITERATIONS)

                .instrumentedBy(new ParametrizedPerformanceSuite<>())
                    .addParameter("First Object", ONE)
                    .addParameter("Second Object", TWO)
                    .addParameter("Third Object", THREE)

                .executeTest("SIMPLE", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        countingMap.add(param);
                        return null;
                    }
                }).whenever(printout).use(StringTableViewer.INSTANCE);

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS, countingMap.getCount(ONE));
        assertEquals(ITERATIONS, countingMap.getCount(TWO));
        assertEquals(ITERATIONS, countingMap.getCount(THREE));
    }

    @Test
    public void shouldAssertOverDifferentParameters() {
        PerformanceTimerFactory.createSingleThreaded()
                .setIterations(1_000)

                .instrumentedBy(new ParametrizedPerformanceSuite<>())
                    .addParameter("First", 10)
                    .addParameter("Second", 35)
                    .addParameter("Third", 100)

                .addPerformanceConsumer(AssertPerformance.withTolerance(5)
                    .assertPercentageFor("First").sameAs(10)
                    .assertPercentageFor("Second").sameAs(35)
                    .assertPercentageFor("Third").sameAs(100))

                .executeTest("ASSERTION", new ParametrizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                }).whenever(printout).use(StringTableViewer.INSTANCE);
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                    .setSamplesPerStep(SAMPLES)
                    .build())

                .instrumentedBy(new ParametrizedPerformanceSuite<>()
                    .addParameter("First", ONE)
                    .addParameter("Second", TWO)
                    .addParameter("Third", THREE))

                .executeTest("PROGRESSION", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        bag.add(param);
                        return null;
                    }
                }).whenever(printout).use(StringTableViewer.INSTANCE);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(times, bag.getCount(ONE));
        assertEquals(times, bag.getCount(TWO));
        assertEquals(times, bag.getCount(THREE));
    }
}
