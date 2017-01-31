package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati
 */
public class NullTest extends AbstractTestable {
    public static final NullTest INSTANCE = new NullTest();

    private NullTest() {}

    @Override
    public Object test() {
        return null;
    }
}
