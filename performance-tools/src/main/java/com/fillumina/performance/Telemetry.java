package com.fillumina.performance;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.StopWatchTimer;

/**
 * Evaluates the percentage of time spent by different parts of a code in a
 * working environment without having the hassle to use a full blown
 * profiler.
 * <p>
 * It allows to better understand which part of an execution takes the most.
 * Because it uses a {@link ThreadLocal} it can be
 * used in a multi-threaded environment (i.e. in a web server where it can
 * trace a single request while other requests are being executed at the same
 * time).
 * <p>
 * All the static methods return a {@code boolean}
 * so they can be used within a Java assertion
 * which will not by default be executed by the JVM. This allows to leave
 * the performance testing code in place without impacting production code.
 * The returned value is always {@code true} to make the assertion succeed.
 * <pre>
 * assert Telemetry.section("calculation");
 * </pre>
 * This is an example:
 * <pre>
public class TelemetryTest {
    private static final int ITERATIONS = 100;
    private static final String START = "START";
    private static final String ONE = "ONE";
    private static final String TWO = "TWO";
    private static final String REPEATING = "REPEATING";
    private static final String THREE = "THREE";

    private boolean printout = false;

    public static void main(final String[] args) {
        final TelemetryTest tt = new TelemetryTest();
        tt.printout = true;
        tt.shouldReturnValidResults();
    }

    void process() {
        Telemetry.start();

        Telemetry.section(START);

        stepOne();
        Telemetry.section(ONE);

        stepTwo();
        Telemetry.section(TWO);

        for (int i=0; i<10; i++) {
            stepRepeating();
        }
        Telemetry.section(REPEATING, 10);

        stepThree();
        Telemetry.section(THREE);
    }

    void stepOne() {
        PerformanceTimeHelper.sleepMicroseconds(20);
    }

    void stepTwo() {
        PerformanceTimeHelper.sleepMicroseconds(10);
    }

    void stepRepeating() {
        PerformanceTimeHelper.sleepMicroseconds(10);
    }

    void stepThree() {
        PerformanceTimeHelper.sleepMicroseconds(100);
    }

    &#64;Test
    public void shouldReturnValidResults() {
        Telemetry.init();
        for (int i=0; i&lt;ITERATIONS; i++) {
            process();
        }
        Telemetry.getStats()
                .printTo(printout)
                .use(AssertPerformance.withTolerance(5)
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(ONE).sameAs(20)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(REPEATING).sameAs(10)
                    .assertPercentage(THREE).sameAs(100));
    }

    &#64;Test
    public void shouldNotWorkAtAllIfNotInitialized() {
        //Telemetry.init();
        for (int i=0; i&lt;ITERATIONS; i++) {
            process();
        }
        assertTrue(Telemetry.getStats().isNull());
    }
 }
 </pre>
 *
 * @author Francesco Illuminati
 */
public class Telemetry {

    private static final ThreadLocal<StopWatchTimer>
            THREAD_LOCAL_TELEMETRY = new ThreadLocal<>();


    /**
     * Initialize the test. Must be called once before the test starts.
     * If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        THREAD_LOCAL_TELEMETRY.set(new StopWatchTimer());
        return true;
    }

    /**
     * Begin a new iteration of the program execution.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean start() {
        StopWatchTimer telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry != null) {
            telemetry.start();
        }
        return true;
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String name, final int iterations) {
        StopWatchTimer telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry != null) {
            telemetry.section(name, iterations);
        }
        return true;
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String name) {
        return section(name, 1);
    }

    /**
     * End the performance sampling process and return the statistics.
     *
     * @param confidence the required confidence of the returned measure
     * @return the statistics
     */
    public static PHolder<SpeedStats> stopAndGetSpeedStats() {
        StopWatchTimer stopWatchTimer = THREAD_LOCAL_TELEMETRY.get();
        THREAD_LOCAL_TELEMETRY.set(null);
        if (stopWatchTimer != null) {
            return stopWatchTimer.getSpeedStats();
        }
        return PHolder.empty();
    }

}
