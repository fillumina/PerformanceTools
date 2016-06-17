package com.fillumina.performance.speed.sample;

/**
 * Holds the iteration performance sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface IterationTime {

    /** The number of iterations executed. */
    long getIterations();

    /** The total time spent iterating. */
    long getTime();

    /** The average time per single iteration. */
    double getTimePerIteration();
}
