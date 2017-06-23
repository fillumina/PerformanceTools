package com.fillumina.performance.mock;

import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.time.sample.iterator.SingleThreadPerformanceExecutor;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It returns
 * pre-defined {@link SpeedSample}.
 *
 * @author Francesco Illuminati
 */
public abstract class PerformanceTimerMock extends DefaultPerformanceTimer {

    public PerformanceTimerMock() {
        this(new SingleThreadPerformanceExecutor());
    }

    public PerformanceTimerMock(PerformanceExecutor executor) {
        super(executor);
    }

    /**
     * Returns fake data.
     */
    @Override
    public SpeedSample iterate(int[] iterations) {
        return createFakePerformances(iterations);
    }

    public abstract SpeedSample createFakePerformances(int[] iterations);
}
