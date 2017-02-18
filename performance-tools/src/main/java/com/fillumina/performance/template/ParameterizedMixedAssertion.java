package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedPerformance;
import com.fillumina.performance.assertion.ParameterizedAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedMixedAssertion
        extends MixedAssertion
            <ParameterizedAssertion<ParameterizedMixedAssertion, SpeedStats>,
             ParameterizedAssertion<ParameterizedMixedAssertion, MemStats>> {

    public AssertParameterizedPerformance<ParameterizedMixedAssertion, SpeedStats>
            speed() {
        if (speed == null) {
            speed = new ParameterizedAssertion<>(this);
        }
        return speed;
    }

    public AssertParameterizedPerformance<ParameterizedMixedAssertion, MemStats>
            usedMem() {
        if (usedMem == null) {
            usedMem =  new ParameterizedAssertion<>(this);
        }
        return usedMem;
    }

    public AssertParameterizedPerformance<ParameterizedMixedAssertion, MemStats>
            allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new ParameterizedAssertion<>(this);
        }
        return allocatedMem;
    }
}
