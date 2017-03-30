package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.Testable;

/**
 * Do nothing test, use only with mocks.
 * <b>This test will be evicted by the JVM.</b>
 *
 * @author Francesco Illuminati
 */
public class NullTestable extends Testable {
    public static final NullTestable INSTANCE = new NullTestable();

    private NullTestable() {}

    @Override
    public void test() {
    }
}
