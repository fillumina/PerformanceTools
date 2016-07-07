package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.mem.AssertMemory;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParametrizedAssertion {
    private AssertParametrizedPerformance<Void, SpeedStats> speed;
    private AssertParametrizedPerformance<Void, MemStats> used;
    private AssertParametrizedPerformance<Void, MemStats> allocated;

    public AssertParametrizedPerformance<Void, SpeedStats> speed() {
        if (speed == null) {
            speed = AssertSpeed.parametrized();
        }
        return speed;
    }

    public AssertParametrizedPerformance<Void, MemStats> memUsed() {
        if (used == null) {
            used = AssertMemory.parametrized();
        }
        return used;
    }

    public AssertParametrizedPerformance<Void, MemStats> memAllocated() {
        if (allocated == null) {
            allocated = AssertMemory.parametrized();
        }
        return allocated;
    }

    AssertParametrizedPerformance<Void, SpeedStats> getSpeedAssertions() {
        return speed;
    }

    AssertParametrizedPerformance<Void, MemStats> getUsedMemoryAssertions() {
        return used;
    }

    AssertParametrizedPerformance<Void, MemStats> getAllocatedMemoryAssertions() {
        return allocated;
    }

    boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
