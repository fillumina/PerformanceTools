package com.fillumina.performance.util;


/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimedRunnable implements Runnable {
    private final int millis;

    public TimedRunnable(int millis) {
        this.millis = millis;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}
