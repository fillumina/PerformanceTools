package com.fillumina.performance.producer.timer;

/**
 * Defines objects that are able to set the number of iterations.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface IterationSettable<T extends IterationSettable<T>> {

    /**
     * How many times each test is repeated in order to get
     * a more accurate result.
     * <br />
     * This value <b>could be overwritten</b> by many of the
     * {@link com.fillumina.performance.producer.PerformanceExecutorInstrumenter}s.
     */
    @SuppressWarnings(value = "unchecked")
    T setIterations(final long iterations);
}
