package com.fillumina.performance;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.time.stats.StopWatchTimer;

/**
 * Evaluates the percentage of time spent by different parts of a code.
 * It can be used in a multi-threaded environment (i.e. tracing a single
 * request in a web server).
 * <p>
 * All static methods return a {@code boolean} so they can be used in
 * an {@code assert} which will not by default be executed by the JVM.
 * This allows to leave the performance testing code in place without
 * impacting production code.
 * <pre>
 * assert Telemetry.section("calculation");
 * </pre>
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
        StopWatchTimer timer = new StopWatchTimer();
        THREAD_LOCAL_TELEMETRY.set(timer);
        return true;
    }

    /**
     * Reset current statistics.
     */
    public static boolean reset() {
        THREAD_LOCAL_TELEMETRY.get().reset();
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
    // TODO should get stats from ALL threads?
    @SuppressWarnings("unchecked")
    public static MixedStatsHolder stopAndGetStats() {
        StopWatchTimer stopWatchTimer = THREAD_LOCAL_TELEMETRY.get();
        THREAD_LOCAL_TELEMETRY.set(null);
        if (stopWatchTimer != null) {
            return stopWatchTimer.getPerformances();
        }
        return MixedStatsHolder.EMPTY;
    }

}
