package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedSequencePerformance;
import com.fillumina.performance.assertion.AssertParametrizedSequencePerformanceImpl;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedSequenceAssertion
        extends MixedAssertion
            <AssertParametrizedSequencePerformanceImpl<Void, SpeedStats>,
             AssertParametrizedSequencePerformanceImpl<Void, MemStats>>{

    public AssertParametrizedSequencePerformance<Void, SpeedStats> speed() {
        if (speed == null) {
            speed = AssertSpeed.parametrizedSequence();
        }
        return speed;
    }

    public AssertParametrizedSequencePerformance<Void, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = AssertMemory.parametrizedSequence();
        }
        return usedMem;
    }

    public AssertParametrizedSequencePerformance<Void, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = AssertMemory.parametrizedSequence();
        }
        return allocatedMem;
    }
}
