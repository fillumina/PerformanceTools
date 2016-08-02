package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.assertion.AssertParametrizedPerformanceImpl;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedAssertion
        extends MixedAssertion
            <AssertParametrizedPerformanceImpl<Void, SpeedStats>,
             AssertParametrizedPerformanceImpl<Void, MemStats>> {

    public AssertParametrizedPerformance<Void, SpeedStats> speed() {
        if (speed == null) {
            speed = AssertSpeed.parametrized();
        }
        return speed;
    }

    public AssertParametrizedPerformance<Void, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = AssertMemory.parametrized();
        }
        return usedMem;
    }

    public AssertParametrizedPerformance<Void, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = AssertMemory.parametrized();
        }
        return allocatedMem;
    }
}
