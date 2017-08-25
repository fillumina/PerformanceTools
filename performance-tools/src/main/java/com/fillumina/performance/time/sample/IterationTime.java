package com.fillumina.performance.time.sample;

/**
 * Holds the iteration performance sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated // use SpeedSingleSample
public interface IterationTime {

    /** @return the number of iterations executed. */
    long getIterations();

    /** @return the total time spent iterating. */
    long getTimeNs();

    /** @returb the average time per single iteration. */
    default double getTimePerIterationNs() {
        return getTimeNs() * 1.0 / getIterations();
    }

    /** @return the frequency. */
    default double getFrequency() {
        return getIterations() * 1E9 / getTimeNs();
    }
}
