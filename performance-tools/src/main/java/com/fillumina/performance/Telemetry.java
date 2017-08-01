package com.fillumina.performance;

import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.time.stats.StopWatchTimer;

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
 </pre>
 *
 * @author Francesco Illuminati
 */
// TODO add a way to call a specific test (main is ok) from within the program (without requiring compilation)
// TODO write a new example
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
        StopWatchTimer timer = new StopWatchTimer();
        THREAD_LOCAL_TELEMETRY.set(timer);
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
    @SuppressWarnings("unchecked")
    public static MixedAssertableHolder stopAndGetStats() {
        StopWatchTimer stopWatchTimer = THREAD_LOCAL_TELEMETRY.get();
        THREAD_LOCAL_TELEMETRY.set(null);
        if (stopWatchTimer != null) {
            return stopWatchTimer.getPerformances();
        }
        return MixedAssertableHolder.EMPTY;
    }

}
