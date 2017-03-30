package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It returns
 * pre-defined {@link SpeedSample}.
 *
 * @author Francesco Illuminati
 */
public abstract class MockPerformanceTimer extends DefaultPerformanceTimer {

    public MockPerformanceTimer() {
        this(new SingleThreadPerformanceExecutor());
    }

    public MockPerformanceTimer(PerformanceExecutor executor) {
        super(executor);
    }

    /**
     * Returns fake data.
     */
    @Override
    public SpeedSample execute(int[] iterations) {
        return createFakePerformances(iterations);
    }

    public abstract SpeedSample createFakePerformances(int[] iterations);
}
