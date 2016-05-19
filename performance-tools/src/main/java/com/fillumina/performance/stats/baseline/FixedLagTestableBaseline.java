package com.fillumina.performance.stats.baseline;

import com.fillumina.performance.sample.AbstractTestable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FixedLagTestableBaseline extends AbstractTestable
        implements TestableBaseline {
    public static final FixedLagTestableBaseline INSTANCE =
            new FixedLagTestableBaseline();
    private final long nanoseconds;

    protected FixedLagTestableBaseline() {
        this(100_000);
    }

    public FixedLagTestableBaseline(long nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    @Override
    public Object test() {
        final long endTime = System.nanoTime() + nanoseconds;
        while(System.nanoTime() < endTime) {}
        return endTime;
    }

    @Override
    public int getNanoseconds() {
        return (int) nanoseconds;
    }
}
