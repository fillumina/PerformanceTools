package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertion<S extends Assertion<?>, M extends Assertion<?>> {
    protected S speed;
    protected M usedMem;
    protected M allocatedMem;

    S getSpeedAssertions() {
        return speed;
    }

    M getUsedMemoryAssertions() {
        return usedMem;
    }

    M getAllocatedMemoryAssertions() {
        return allocatedMem;
    }

}
