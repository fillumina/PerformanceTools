package com.fillumina.performance.speed.sample;

import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It returns
 * pre-defined {@link SpeedSample}.
 *
 * @author Francesco Illuminati
 */
public abstract class FakePerformanceTimer extends DefaultPerformanceTimer {

    public FakePerformanceTimer() {
        super(new SingleThreadPerformanceExecutor());
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
