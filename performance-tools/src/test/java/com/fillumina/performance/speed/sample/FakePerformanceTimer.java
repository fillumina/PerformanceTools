package com.fillumina.performance.speed.sample;

import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It returns
 * pre-defined {@link PerformanceSample}.
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
    public PerformanceSample execute(int[] iterations) {
        return createFakePerformances(iterations);
    }

    public abstract PerformanceSample createFakePerformances(int[] iterations);
}
