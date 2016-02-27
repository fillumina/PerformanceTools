package com.fillumina.performance.util;

import com.fillumina.performance.producer.timer.AbstractTestable;

/**
 *
 * @author Francesco Illuminati
 */
public class EmptyTestable extends AbstractTestable {

    public static final EmptyTestable INSTANCE = new EmptyTestable();

    private EmptyTestable() {}

    @Override
    public Object test() {
        return null;
    }
}
