package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 * It's a fake {@link PerformanceTimer} to help testing. It operates
 * in the same way as the real one with the exception that the returned
 * {@link LoopPerformances} can be defined beforehand.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class FakePerformanceTimer extends PerformanceTimer {
    private static final long serialVersionUID = 1L;

    public FakePerformanceTimer() {
        super(new SingleThreadPerformanceExecutor());
    }

    /**
     * Does the same steps as the real {@link PerformanceTimer} so
     * it can be used interchangeably for most tests.
     */
    @Override
    public LoopPerformancesHolder execute() {
        super.execute();
        return new LoopPerformancesHolder(getLoopPerformances(getIterations()));
    }

    public abstract LoopPerformances getLoopPerformances(
            final long iterations);

}
