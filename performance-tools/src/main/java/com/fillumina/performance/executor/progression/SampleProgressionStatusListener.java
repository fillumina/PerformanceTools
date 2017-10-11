package com.fillumina.performance.executor.progression;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface SampleProgressionStatusListener {

    void acceptSampleProgressionStatus(SampleProgressionStatus status);

    SampleProgressionStatusListener NULL = s -> {};
}
