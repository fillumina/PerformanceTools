package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeTest extends AbstractTestable {
    private final int millis;

    public TimeTest(int millis) {
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
