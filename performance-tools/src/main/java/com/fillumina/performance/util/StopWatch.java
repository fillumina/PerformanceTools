package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatch {

    private long startNs = -1;

    public void start() {
        startNs = System.nanoTime();
    }

    public long stop() {
        return System.nanoTime() - startNs;
    }

    public boolean isRunning() {
        return startNs != -1;
    }
}
