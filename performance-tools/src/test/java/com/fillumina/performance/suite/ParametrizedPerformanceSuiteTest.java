package com.fillumina.performance.suite;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.ComposedName;
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

        Map<ComposedName, SpeedStats> map =
            PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(ITERATIONS)
                        .setSamples(SAMPLES)
                        .build())
                .instrumentedBy(SpeedSuite.<String>parametrizedSuite())
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

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(ONE));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(TWO));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(THREE));
    }

    @Test
    public void shouldRunTwoTestsWithSameParameters() {
        final Bag<String> countingBag = new Bag<>();

        Map<ComposedName, SpeedStats> map =
            PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(ITERATIONS)
                        .setSamples(SAMPLES)
                        .build())
                    .instrumentedBy(SpeedSuite.<String>parametrizedSuite())
                    .setName("Two Tests with same paramenters")
                    .addParameter("param1", ONE)
                    .addParameter("param2", TWO)
                    .addParameter("param3", THREE)

                .addTest("FirstTest", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        countingBag.add("FirstTest" + param);
                        return null;
                    }
                })
                .addTest("SecondTest", new ParametrizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        countingBag.add("SecondTest" + param);
                        return null;
                    }
                })

                .execute()
                .printIf(printout)
                .getPerformance();

        assertEquals(6, countingBag.size());

        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + ONE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + TWO));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + THREE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + ONE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + TWO));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + THREE));
    }

    @Test
    public void shouldAssertOverDifferentParameters() {
        PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(30)
                        .build())
                    .instrumentedBy(SpeedSuite.<Integer>parametrizedSuite())
                    .addParameter("First", 10)
                    .addParameter("Second", 35)
                    .addParameter("Third", 100)

                .addTest("sleep test", new ParametrizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                })

                .addPerformanceConsumer(AssertSpeed.parametrized()
                        .forTest("sleep test",
                                AssertSpeed.withTolerance(5)
                                    .assertPercentage("First").sameAs(10)
                                    .assertPercentage("Second").sameAs(35)
                                    .assertPercentage("Third").sameAs(100)))

                .execute()

                .printIf(printout);
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                        .setSamples(SAMPLES)
                        .build())
                    .instrumentedBy(SpeedSuite.<String>parametrizedSuite()
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

                .printIf(printout);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(bag.toString(), times, bag.getCount(ONE));
        assertEquals(bag.toString(), times, bag.getCount(TWO));
        assertEquals(bag.toString(), times, bag.getCount(THREE));
    }
}
