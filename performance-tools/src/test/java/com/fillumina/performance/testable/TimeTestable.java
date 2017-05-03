package com.fillumina.performance.testable;


/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeTestable implements Runnable {
    private final int millis;

    public TimeTestable(int millis) {
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
