package com.fillumina.performance.sample.executor;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.DefaultPerformanceTimer;

/**
 * It's a fake {@link DefaultPerformanceTimer} to help testing. It operates
 * in the same way as the real one with the exception that the returned
 * {@link PerformanceSample} can be defined beforehand.
 *
 * @author Francesco Illuminati
 */
public abstract class FakePerformanceTimer extends DefaultPerformanceTimer {

    public FakePerformanceTimer() {
        super(new SingleThreadPerformanceExecutor());
    }

    /**
     * Does the same steps as the real {@link DefaultPerformanceTimer} so
     * it can be used interchangeably for most tests but returns fake data.
     */
    @Override
    public PerformanceSample execute(int iterations) {
        super.execute(iterations);
        return createFakePerformances(iterations);
    }

    public abstract PerformanceSample createFakePerformances(
            final long iterations);
}
