package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedSequencePerformance;
import com.fillumina.performance.assertion.ParameterizedSequenceAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceMixedAssertion
        extends MixedAssertion
            <ParameterizedSequenceAssertion
                <ParameterizedSequenceMixedAssertion, SpeedStats>,
             ParameterizedSequenceAssertion
                <ParameterizedSequenceMixedAssertion, MemStats>> {

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceMixedAssertion, SpeedStats> speed() {
        if (speed == null) {
            speed = new ParameterizedSequenceAssertion<>(this);
        }
        return speed;
    }

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceMixedAssertion, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = new ParameterizedSequenceAssertion<>(this);
        }
        return usedMem;
    }

    public AssertParameterizedSequencePerformance
                <ParameterizedSequenceMixedAssertion, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new ParameterizedSequenceAssertion<>(this);
        }
        return allocatedMem;
    }
}
