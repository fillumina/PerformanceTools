package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.Sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TimeSampleBuilder {

    Sample buildAverageTimeSample();

    Sample buildThroughputSample();

    long getTotalTimeNs();

    boolean isEmpty();
}
