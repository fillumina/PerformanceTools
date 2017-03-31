package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sleeper {

    public static void sleepSeconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1_000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
