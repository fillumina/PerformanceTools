package com.fillumina.performance.util;

/**
 *
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
