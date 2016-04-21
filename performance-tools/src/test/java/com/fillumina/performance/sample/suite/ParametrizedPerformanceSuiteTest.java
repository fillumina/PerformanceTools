package com.fillumina.performance.sample.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.viewer.StringTableSampleViewer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
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

    // they are primes to allow unique results
    private static final int SAMPLES = 3;
    private static final int ITERATIONS = 7;
    private static final int FIRST_ITERATION = 5;
    private static final int SECOND_ITERATION = 11;

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

        PerformanceSample sample = PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(new ParametrizedPerformanceSuite<String>())
                    .addParameter("First Object", ONE)
                    .addParameter("Second Object", TWO)
                    .addParameter("Third Object", THREE)

                .addTest("SIMPLE", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        countingMap.add(param);
                        return null;
                    }
                })

                .execute(ITERATIONS);

        if (printout) {
            StringTableSampleViewer.INSTANCE.consume(null, sample);
        }

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS, countingMap.getCount(ONE));
        assertEquals(ITERATIONS, countingMap.getCount(TWO));
        assertEquals(ITERATIONS, countingMap.getCount(THREE));
    }

    @Test
    public void shouldAssertOverDifferentParameters() {
        PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(new ParametrizedPerformanceSuite<Integer>())
                    .addParameter("First", 10)
                    .addParameter("Second", 35)
                    .addParameter("Third", 100)

                .addTest("ASSERTION", new ParametrizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                })

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(30)
                    .build())

                .addPerformanceConsumer(AssertPerformance.withTolerance(5)
                    .assertPercentageFor("ASSERTION_First").sameAs(10)
                    .assertPercentageFor("ASSERTION_Second").sameAs(35)
                    .assertPercentageFor("ASSERTION_Third").sameAs(100))

                .execute()

                .whenever(printout)

                .use(StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(new ParametrizedPerformanceSuite<String>()
                    .addParameter("First", ONE)
                    .addParameter("Second", TWO)
                    .addParameter("Third", THREE))

                .addTest("PROGRESSION", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        bag.add(param);
                        return null;
                    }
                })

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                    .setSamplesPerStep(SAMPLES)
                    .build())

                .execute()

                .whenever(printout)

                .use(StringTableStatsViewer.INSTANCE);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(bag.toString(), times, bag.getCount(ONE));
        assertEquals(bag.toString(), times, bag.getCount(TWO));
        assertEquals(bag.toString(), times, bag.getCount(THREE));
    }
}
