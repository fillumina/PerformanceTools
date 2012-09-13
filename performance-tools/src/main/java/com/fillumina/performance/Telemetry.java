package com.fillumina.performance;

import com.fillumina.performance.producer.RunningLoopPerformances;
import com.fillumina.performance.consumer.viewer.StringTableViewer;

/**
 *
 * @author fra
 */
//TODO: make a test for this
public class Telemetry {

    private static ThreadLocal<Telemetry> threadLocal = new ThreadLocal<>();
    private long last;
    private final RunningLoopPerformances runningPerf;

    public Telemetry(final long iterations) {
        runningPerf = new RunningLoopPerformances(iterations);
        last = System.nanoTime();
    }

    /**
     * Initialize the test. If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init(final long iterations) {
        threadLocal.set(new Telemetry(iterations));
        return true;
    }

    /** @return always true so that it can be put on an assert */
    public static boolean segment(final String name) {
        final Telemetry telemetry = threadLocal.get();
        if (telemetry != null) {
            telemetry.localSegment(name);
        }
        return true;
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

    @Override
    public String toString() {
        return new StringTableViewer(runningPerf.getLoopPerformances())
                .getTable()
                .toString();
    }
}