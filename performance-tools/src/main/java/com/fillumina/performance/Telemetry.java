package com.fillumina.performance;

import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.StopWatchTimer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * so that they can be used within a Java assertion
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
        Telemetry.getTree()
                .printIf(printout)
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
        assertTrue(Telemetry.getTree().isEmpty());
    }
 }
 </pre>
 *
 * @author Francesco Illuminati
 */
public class Telemetry {
    public static final String DEFAULT_NAME = "default";

    private static final Map<String,ThreadLocal<StopWatchTimer>>
            THREAD_LOCAL_TELEMETRY_MAP = new ConcurrentHashMap<>();

    /**
     * Initialize the test. Must be called once before the test starts.
     * If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        return init(DEFAULT_NAME);
    }

    /**
     * Initialize the test. Must be called once before the test starts.
     * If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @param telemetryName allows to use many Telemetries at the same time
     * by specifying a different name for each.
     * @return always true so that it can be put on an assert
     */
    public static boolean init(String telemetryName) {
        getThreadLocal(telemetryName).set(new StopWatchTimer());
        return true;
    }

    private static ThreadLocal<StopWatchTimer> getThreadLocal(String name) {
        ThreadLocal<StopWatchTimer> tl = THREAD_LOCAL_TELEMETRY_MAP.get(name);
        if (tl == null) {
            tl = new ThreadLocal<>();
            THREAD_LOCAL_TELEMETRY_MAP.put(name, tl);
        }
        return tl;
    }

    /**
     * Begin a new iteration of the program execution.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean start() {
        return start(DEFAULT_NAME);
    }

    /**
     * Begin a new iteration of the program execution.
     *
     * @param telemetryName allows to use many Telemetries at the same time
     * by specifying a different name for each.
     * @return always true so that it can be put on an assert
     */
    public static boolean start(String name) {
        StopWatchTimer telemetry = getThreadLocal(name).get();
        if (telemetry != null) {
            telemetry.start();
        }
        return true;
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @param sectionName the name of the section to measure
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String sectionName) {
        return section(sectionName, 1);
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @param telemetryName the name of the telemetry to use
     * @param sectionName the name of the section to measure
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String telemetryName,
            final String sectionName) {
        return section(telemetryName, sectionName, 1);
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @param sectionName the name of the section to measure
     * @param iterations how many times the section is executed
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String sectionName,
            final int iterations) {
        return section(DEFAULT_NAME, sectionName, iterations);
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @param telemetryName allows to use many Telemetries at the same time
     * by specifying a different name for each.
     * @param sectionName the name of the section to measure
     * @param iterations how many times the section is executed
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String telemetryName,
            final String sectionName,
            final int iterations) {
        StopWatchTimer telemetry = getThreadLocal(telemetryName).get();
        if (telemetry != null) {
            telemetry.section(sectionName, iterations);
        }
        return true;
    }

    /**
     * End the performance sampling process and return the statistics.
     *
     * @return the statistics
     */
    public static TreeHolder<SpeedStats, SpeedStats> stop() {
        return stop(DEFAULT_NAME);
    }

    /**
     * End the performance sampling process and return the statistics.
     *
     * @param telemetryName allows to use many Telemetries at the same time
     * by specifying a different name for each.
     * @return the statistics
     */
    public static TreeHolder<SpeedStats, SpeedStats> stop(String telemetryName) {
        StopWatchTimer stopWatchTimer = getThreadLocal(telemetryName).get();
        getThreadLocal(telemetryName).set(null);
        if (stopWatchTimer != null) {
            return stopWatchTimer.getPerformance();
        }
        return TreeHolder.empty();
    }

}
