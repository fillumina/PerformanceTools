package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformance;
import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceAssertion
        extends MixedAssertion
            <AssertParameterizedSequencePerformanceImpl
                <ParameterizedSequenceAssertion, SpeedStats>,
             AssertParameterizedSequencePerformanceImpl
                <ParameterizedSequenceAssertion, MemStats>> {

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceAssertion, SpeedStats> speed() {
        if (speed == null) {
            speed = new AssertParameterizedSequencePerformanceImpl<>(this);
        }
        return speed;
    }

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceAssertion, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = new AssertParameterizedSequencePerformanceImpl<>(this);
        }
        return usedMem;
    }

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceAssertion, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new AssertParameterizedSequencePerformanceImpl<>(this);
        }
        return allocatedMem;
    }
}
