package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterized;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.assertion.ParameterizedAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedMixedAssertion
        extends MixedAssertion
            <AssertParameterized<ParameterizedMixedAssertion, SpeedStats>,
             AssertParameterized<ParameterizedMixedAssertion, MemStats>> {

    public ParameterizedAssertion<ParameterizedMixedAssertion, SpeedStats>
            speed() {
        if (speed == null) {
            speed = new AssertParameterized<>(this);
        }
        return speed;
    }

    public ParameterizedAssertion<ParameterizedMixedAssertion, MemStats>
            usedMem() {
        if (usedMem == null) {
            usedMem =  new AssertParameterized<>(this);
        }
        return usedMem;
    }

    public ParameterizedAssertion<ParameterizedMixedAssertion, MemStats>
            allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new AssertParameterized<>(this);
        }
        return allocatedMem;
    }
}
