package com.fillumina.performance.stats.baseline;

import com.fillumina.performance.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NullBaseline extends AbstractTestable
        implements TestableBaseline {

    public static final NullBaseline INSTANCE =
            new NullBaseline();

    protected NullBaseline() {}

    @Override
    public Object test() {
        return null;
    }

    @Override
    public int getNanoseconds() {
        return 0;
    }
}
