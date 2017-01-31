package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.assertion.AssertParameterizedPerformance;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedAssertion
        extends MixedAssertion
            <AssertParameterizedPerformanceImpl<Void, SpeedStats>,
             AssertParameterizedPerformanceImpl<Void, MemStats>> {

    public AssertParameterizedPerformance<Void, SpeedStats> speed() {
        if (speed == null) {
            speed = AssertSpeed.parameterized();
        }
        return speed;
    }

    public AssertParameterizedPerformance<Void, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = AssertMemory.parameterized();
        }
        return usedMem;
    }

    public AssertParameterizedPerformance<Void, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = AssertMemory.parameterized();
        }
        return allocatedMem;
    }
}
