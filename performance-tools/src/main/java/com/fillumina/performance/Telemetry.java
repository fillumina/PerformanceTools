package com.fillumina.performance;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.time.stats.StopWatchTimer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Evaluates the percentage of time spent by different parts of a code.
 * It can be used in a multi-threaded environment (i.e. tracing a single
 * request on a web server).
 * <pre>
 * assert Telemetry.section("calculation");
 * </pre>
 *
 * @author Francesco Illuminati
 */
public class Telemetry {

    private static final ThreadLocal<StopWatchTimer>
            THREAD_LOCAL_TELEMETRY = new ThreadLocal<>();

    private static final Map<String, StopWatchTimer> MAP =
            new ConcurrentHashMap<>();

    /**
     * Initializes the test. Must be called once before the test starts.
     * If it is not called all the other calls will be ignored.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        StopWatchTimer timer = new StopWatchTimer();
        THREAD_LOCAL_TELEMETRY.set(timer);
        MAP.put(Thread.currentThread().getName(), timer);
        return true;
    }

    /**
     * Resets current statistics. It should be called after warmup.
     */
    public static boolean reset() {
        THREAD_LOCAL_TELEMETRY.get().reset();
        return true;
    }

    /**
     * Begins a new iteration of the program execution.
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
    public static boolean section(final String name) {
        return section(name, 1);
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
     * End the performance sampling process and return the statistics.
     *
     * @param confidence the required confidence of the returned measure
     * @return the statistics
     */
    @SuppressWarnings("unchecked")
    public static MixedStatsHolder stopAndGetStats() {
        StopWatchTimer stopWatchTimer = THREAD_LOCAL_TELEMETRY.get();
        THREAD_LOCAL_TELEMETRY.set(null);
        if (stopWatchTimer != null) {
            return stopWatchTimer.getPerformances();
        }
        return MixedStatsHolder.EMPTY;
    }

    /**
     * @return the statistics map of all threads.
     */
    public static Map<String, MixedStatsHolder> getStatsFromAllThreads() {
        Map<String,MixedStatsHolder> result = new HashMap<>();
        MAP.forEach((String name, StopWatchTimer timer) ->
                result.put(name, timer.getPerformances()));
        return result;
    }

    /**
     * Reset the statistics for ALL threads.
     */
    public static void clear() {
        MAP.values().forEach(timer -> timer.reset());
        MAP.clear();
    }
}
