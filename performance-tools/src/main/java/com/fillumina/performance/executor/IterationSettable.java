package com.fillumina.performance.executor;

/**
 * Most {@link PerformanceExecutor}s need the number of iteration to perform.
 *
 * @author Francesco Illuminati
 */
public interface IterationSettable<T extends IterationSettable<T>> {

    /**
     * How many times each test is repeated in order to get
     * a more accurate result.
     * <br>
     * This value <b>could be overwritten</b> by many of the
     * {@link com.fillumina.performance.producer.PerformanceExecutorInstrumenter}s.
     */
    T setIterations(final int iterations);
}
