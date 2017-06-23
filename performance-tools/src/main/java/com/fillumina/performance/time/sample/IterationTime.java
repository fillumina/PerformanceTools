package com.fillumina.performance.time.sample;

/**
 * Holds the iteration performance sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface IterationTime {

    /** The number of iterations executed. */
    long getIterations();

    /** The total time spent iterating. */
    long getTimeNs();

    /** The average time per single iteration. */
    default double getTimePerIterationNs() {
        return getTimeNs() * 1.0 / getIterations();
    }
}
