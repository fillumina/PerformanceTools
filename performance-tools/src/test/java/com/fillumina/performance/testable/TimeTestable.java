package com.fillumina.performance.testable;

import com.fillumina.performance.infrastructure.AbstractTestable;

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
    public void test() {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}
