package com.fillumina.performance.suite;

import com.fillumina.performance.FakePerformanceExecutor;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedSuite;
import com.fillumina.performance.speed.stats.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Bag;
import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ParameterizedPerformanceSuiteTest {
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";

    // they are primes to allow unique results
    private static final int SAMPLES = 3;
    private static final int ITERATIONS = 7;
    private static final int FIRST_ITERATION = 5;
    private static final int SECOND_ITERATION = 11;

    private Appendable printout;

    public static void main(final String[] args) {
        final ParameterizedPerformanceSuiteTest ppst =
                new ParameterizedPerformanceSuiteTest();
        ppst.printout = System.out;
        ppst.shouldRunTheSameTestOverDifferentParameters();
        //ppst.shouldAssertDifferentTestSeparately();
    }

    @Test
    public void shouldRunTheSameTestOverDifferentParameters() {
        final Bag<String> countingMap = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(ITERATIONS)
                    .setSamples(SAMPLES)
                    .build())
            .instrumentedBy(SpeedSuite.<String>parameterizedSuite())
            .addParameter("First Object", ONE)
            .addParameter("Second Object", TWO)
            .addParameter("Third Object", THREE)

            .addTest("SIMPLE", new ParameterizedTestable<String>() {
                @Override
                public Object test(final String param) {
                    countingMap.add(param);
                    return null;
                }
            })

            .execute()
            .printTo(printout);

        assertEquals(3, countingMap.size());

        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(ONE));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(TWO));
        assertEquals(ITERATIONS * SAMPLES, countingMap.getCount(THREE));
    }

    @Test
    public void shouldRunTwoTestsWithSameParameters() {
        final Bag<String> countingBag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(ITERATIONS)
                    .setSamples(SAMPLES)
                    .build())
                .instrumentedBy(SpeedSuite.<String>parameterizedSuite())
                .setName("Two Tests with same paramenters")
                .addParameter("param1", ONE)
                .addParameter("param2", TWO)
                .addParameter("param3", THREE)

            .addTest("FirstTest", new ParameterizedTestable<String>() {
                @Override
                public Object test(final String param) {
                    countingBag.add("FirstTest" + param);
                    return null;
                }
            })
            .addTest("SecondTest", new ParameterizedTestable<String>() {
                @Override
                public Object test(final String param) {
                    countingBag.add("SecondTest" + param);
                    return null;
                }
            })

            .execute()
            .printTo(printout);

        assertEquals(6, countingBag.size());

        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + ONE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + TWO));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("FirstTest" + THREE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + ONE));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + TWO));
        assertEquals(ITERATIONS * SAMPLES, countingBag.getCount("SecondTest" + THREE));
    }

    @Test
    public void shouldUseTheProgression() {
        final Bag<String> bag = new Bag<>();

        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(FIRST_ITERATION, SECOND_ITERATION)
                        .setSamples(SAMPLES)
                        .build())
                    .instrumentedBy(SpeedSuite.<String>parameterizedSuite()
                    .addParameter("First", ONE)
                    .addParameter("Second", TWO)
                    .addParameter("Third", THREE))

                .addTest("PROGRESSION", new ParameterizedTestable<String>() {
                    @Override
                    public Object test(final String param) {
                        bag.add(param);
                        return null;
                    }
                })

                .execute()

                .printTo(printout);

        assertEquals(3, bag.size());

        final int times = (FIRST_ITERATION + SECOND_ITERATION) * SAMPLES;

        assertEquals(bag.toString(), times, bag.getCount(ONE));
        assertEquals(bag.toString(), times, bag.getCount(TWO));
        assertEquals(bag.toString(), times, bag.getCount(THREE));
    }

    @Test
    public void shouldAssertOverDifferentParameters() {
        PerformanceTimerFactory.createSingleThreaded()

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setIterationProgression(30)
                        .build())
                    .instrumentedBy(SpeedSuite.<Integer>parameterizedSuite())
                    .addParameter("First", 10)
                    .addParameter("Second", 35)
                    .addParameter("Third", 100)

                .addTest("sleep test", new ParameterizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                })

                .execute()

                .check(AssertSpeed.parameterized()
                        .forTest("sleep test")
                            .withTolerance(5)
                                    .assertPercentage("First").sameAs(10)
                                    .assertPercentage("Second").sameAs(35)
                                    .assertPercentage("Third").sameAs(100)
                            .end()
                        .endTests())


                .printTo(printout);
    }

    @Test
    public void shouldAssertDifferentTestSeparately() {
        FakePerformanceExecutor.createPerformanceTimer(new double[][]{
            {100, 10}, {200, 10}
        })

                .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                        .setSamples(33)
                        .build())
                    .instrumentedBy(SpeedSuite.<Integer>parameterizedSuite())
                    .addParameter("First", 1)
                    .addParameter("Second", 2)

                .addTest("testA", new ParameterizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                })
                .addTest("testB", new ParameterizedTestable<Integer>() {
                    @Override
                    public Object test(final Integer param) {
                        sleepMicroseconds(param);
                        return null;
                    }
                })

                .execute()

                .checkAndPrint(printout,
                    AssertSpeed.parameterized()
                        .forTest("testA")
                            .withTolerance(0)
                                .assertOrder("Second").greaterThan("First")
                            .end()
                        .forTest("testB")
                            .withTolerance(0)
                                .assertOrder("First").lessThan("Second")
                            .end()
                        .endTests())

                .printTo(printout);
    }
}