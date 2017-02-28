package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertParameterizedSequence;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.assertion.ParameterizedSequenceAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceMixedAssertion
        extends MixedAssertion
            <AssertParameterizedSequence
                <ParameterizedSequenceMixedAssertion, SpeedStats>,
             AssertParameterizedSequence
                <ParameterizedSequenceMixedAssertion, MemStats>> {

    public ParameterizedSequenceAssertion
                <ParameterizedSequenceMixedAssertion, SpeedStats> speed() {
        if (speed == null) {
            speed = new AssertParameterizedSequence<>(this);
        }
        return speed;
    }

    public ParameterizedSequenceAssertion
                <ParameterizedSequenceMixedAssertion, MemStats> usedMem() {
        if (usedMem == null) {
            usedMem = new AssertParameterizedSequence<>(this);
        }
        return usedMem;
    }

    public ParameterizedSequenceAssertion
                <ParameterizedSequenceMixedAssertion, MemStats> allocatedMem() {
        if (allocatedMem == null) {
            allocatedMem = new AssertParameterizedSequence<>(this);
        }
        return allocatedMem;
    }
}
