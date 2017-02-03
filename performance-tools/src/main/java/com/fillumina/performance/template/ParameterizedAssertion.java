package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedPerformance;
import com.fillumina.performance.assertion.AssertParameterizedPerformanceImpl;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedAssertion
        extends MixedAssertion
            <AssertParameterizedPerformanceImpl
                <ParameterizedAssertion, SpeedStats>,
             AssertParameterizedPerformanceImpl
                <ParameterizedAssertion, MemStats>> {

    public AssertParameterizedPerformance<ParameterizedAssertion, SpeedStats>
            speed() {
        if (speed == null) {
            speed = new AssertParameterizedPerformanceImpl<>(this);
        }
        return speed;
    }

    public AssertParameterizedPerformance<ParameterizedAssertion, MemStats>
            usedMem() {
        if (usedMem == null) {
            usedMem =  new AssertParameterizedPerformanceImpl<>(this);
        }
        return usedMem;
    }

    public AssertParameterizedPerformance<ParameterizedAssertion, MemStats>
            allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new AssertParameterizedPerformanceImpl<>(this);
        }
        return allocatedMem;
    }
}
