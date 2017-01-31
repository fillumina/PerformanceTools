package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformanceImpl;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.assertion.AssertParameterizedSequencePerformance;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceAssertion
        extends MixedAssertion
            <AssertParameterizedSequencePerformanceImpl<Void, SpeedStats>,
             AssertParameterizedSequencePerformanceImpl<Void, MemStats>>{

    public AssertParameterizedSequencePerformance<Void, SpeedStats> speed() {
        if (speed == null) {
            speed = AssertSpeed.parameterizedSequence();
        }
        return speed;
    }

    public AssertParameterizedSequencePerformance<Void, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = AssertMemory.parameterizedSequence();
        }
        return usedMem;
    }

    public AssertParameterizedSequencePerformance<Void, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = AssertMemory.parameterizedSequence();
        }
        return allocatedMem;
    }
}
