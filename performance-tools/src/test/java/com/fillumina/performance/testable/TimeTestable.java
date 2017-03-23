package com.fillumina.performance.testable;

import com.fillumina.performance.infrastructure.Testable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeTestable extends Testable {
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
