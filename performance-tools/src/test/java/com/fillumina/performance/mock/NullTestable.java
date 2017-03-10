package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.AbstractTestable;

/**
 * Do nothing test. Note that this test will be evicted by the JVM.
 *
 * @author Francesco Illuminati
 */
public class NullTestable extends AbstractTestable {
    public static final NullTestable INSTANCE = new NullTestable();

    private NullTestable() {}

    @Override
    public void test() {
    }
}
