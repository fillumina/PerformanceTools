package com.fillumina.performance;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.RunningLoopPerformances;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.LoopPerformances;

/**
 * Allows to evaluate the percentage of time used by different parts of
 * a long execution.
 *
 * The static methods return a boolean so that they can be used with assertion:
 * <pre>
 *      assert Telemetry.segment("calculation");
 * </pre>
 * which will not be executed by the JVM when not in test mode. The returned
 * value is always true.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Telemetry {

    private static ThreadLocal<Telemetry> threadLocal = new ThreadLocal<>();

    private final RunningLoopPerformances runningPerf;
    private long last;
    private long iterations;

    public Telemetry() {
        runningPerf = new RunningLoopPerformances();
        last = System.nanoTime();
    }

    /**
     * Initialize the test. If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        threadLocal.set(new Telemetry());
        return true;
    }

    public static boolean startIteration() {
        getTelemetry().iterations++;
        return true;
    };

    /**
     * @return always true so that it can be put on an assert.
     */
    public static boolean segment(final String name) {
        final Telemetry telemetry = threadLocal.get();
        if (telemetry != null) {
            telemetry.localSegment(name);
        }
        return true;
    }

    public static LoopPerformances getLoopPerformances() {
        final Telemetry telemetry = getTelemetry();
        final RunningLoopPerformances performances = telemetry.runningPerf;
        performances.setIterations(telemetry.iterations);
        return performances.getLoopPerformances();
    }

    public static void use(final PerformanceConsumer consumer) {
        consumer.consume("Telemetry", getLoopPerformances());
    }

    public static void print() {
        System.out.println(threadLocal.get().toString());
    }

    private void localSegment(final String name) {
        final long nano = getSegmentTime();
        runningPerf.add(name, nano);
    }

    private long getSegmentTime() {
        final long nano = System.nanoTime();
        final long segment = nano - last;
        last = nano;
        return segment;
    }

    private static Telemetry getTelemetry() {
        final Telemetry telemetry = threadLocal.get();
        if (telemetry == null) {
            throw new IllegalStateException(
                    "No Telemetry available for this thread");
        }
        return telemetry;
    }

    @Override
    public String toString() {
        return new StringTableViewer(runningPerf.getLoopPerformances())
                .getTable()
                .toString();
    }
}
