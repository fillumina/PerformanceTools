package com.fillumina.performance.testable;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati
 */
public class NullTestable extends AbstractTestable {
    public static final NullTestable INSTANCE = new NullTestable();

    private NullTestable() {}

    @Override
    public Object test() {
        return null;
    }
}
