package com.fillumina.performance.stats.baseline;

import com.fillumina.performance.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SimpleTestableBaseline extends AbstractTestable
        implements TestableBaseline {

    public static final SimpleTestableBaseline INSTANCE =
            new SimpleTestableBaseline();

    private int counter;

    protected SimpleTestableBaseline() {}

    @Override
    public Object test() {
        counter++;
        return counter;
    }

    @Override
    public int getNanoseconds() {
        return 0;
    }
}
