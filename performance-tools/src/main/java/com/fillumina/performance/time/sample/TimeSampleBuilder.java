package com.fillumina.performance.time.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TimeSampleBuilder {

    AverageTimeSample buildAverageTimeSample();

    ThroughputSample buildThroughputSample();

    long getTotalTimeNs();

    boolean isEmpty();
}
