package com.fillumina.performance.testable;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeTestable extends AbstractTestable {
    private final int millis;

    public TimeTestable(int millis) {
        this.millis = millis;
    }

    @Override
    public Object test() {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
        return true;
    }
}
