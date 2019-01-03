package com.fillumina.performance.util;

/**
 * Helper method that uses {@link Thread#sleep(long)} to stop the current
 * thread for the given time. Because it involves calls to the underlined
 * OS it might be not so accurate. In case maximum accuracy is needed use
 * {@link AccurateSleeper}
 *
 * @see AccurateSleeper
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sleeper {

    public static void sleepSeconds(final int seconds) {
        sleepMillis(seconds * 1_000);
    }

    public static void sleepMillis(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
