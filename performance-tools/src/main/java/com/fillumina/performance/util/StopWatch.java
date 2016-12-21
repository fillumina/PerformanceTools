package com.fillumina.performance.util;

/**
 * Stopwatch timer class using nanoseconds.
 * This class is not thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatch {

    private long startNs = -1;

    public void start() {
        startNs = System.nanoTime();
    }

    /** @return the elapsed nanoseconds since {@link #start()}. */
    public long stop() {
        return System.nanoTime() - startNs;
    }

    public boolean isRunning() {
        return startNs != -1;
    }

    public void reset() {
        startNs = -1;
    }
}
