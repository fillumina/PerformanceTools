package com.fillumina.performance.util;

import com.fillumina.performance.sample.AbstractTestable;

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
