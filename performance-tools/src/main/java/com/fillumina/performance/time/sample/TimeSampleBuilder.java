package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.Sample;

/**
 * The same speed sample produces two different kind of measurement:
 * <ol>
 * <li>average execution time, how much time for each operation (s/op)
 * <li>throughput, how many operations in a given time (op/s)
 * </ol>
 * Because statistical operations must be applied to them they must be
 * recorded as different streams of data.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TimeSampleBuilder {

    Sample buildAverageTimeSample();

    Sample buildThroughputSample();

    long getTotalTimeNs();

    boolean isEmpty();
}
