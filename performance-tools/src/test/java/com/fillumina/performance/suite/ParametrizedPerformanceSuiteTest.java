package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.suite.viewer.StringTableParametrizedStatsViewer;
import com.fillumina.performance.util.Bag;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import java.util.Map;
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

        Map<String, PerformanceStats> map =
            PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(ITERATIONS)
                        .setSamplesPerStep(SAMPLES)
                        .build())
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

                .execute()
                .printIf(printout)
                .getPerformance();

        if (printout) {
            //StringTableSampleViewer.INSTANCE.consume(null, sample);
        }

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(ONE));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(TWO));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(THREE));
    }

    @Test
    public void shouldAssertOverDifferentParameters() {
        PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(30)
                        .setAddBaselineTest(false)
                        .build())
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

                //FIXME Assertion
//                .addPerformanceConsumer(AssertPerformance.withTolerance(5)
//                    .assertPercentage("ASSERTION_First").sameAs(10)
//                    .assertPercentage("ASSERTION_Second").sameAs(35)
//                    .assertPercentage("ASSERTION_Third").sameAs(100))

                .execute()

                .whenever(printout);

                //FIXME viewers
//                .use(StringTableStatsViewer.INSTANCE);
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                        .setSamplesPerStep(SAMPLES)
                        .build())
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

                .execute()

                .whenever(printout)

                .use(StringTableParametrizedStatsViewer.INSTANCE);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(bag.toString(), times, bag.getCount(ONE));
        assertEquals(bag.toString(), times, bag.getCount(TWO));
        assertEquals(bag.toString(), times, bag.getCount(THREE));
    }
}
