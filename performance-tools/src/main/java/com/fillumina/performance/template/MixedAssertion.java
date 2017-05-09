package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO generalize this?
public class MixedAssertion<S extends Assertion<?>, M extends Assertion<?>> {
    private S speed;
    private M usedMem;
    private M allocatedMem;

    protected void setSpeed(S speed) {
        this.speed = speed;
    }

    protected void setUsedMem(M usedMem) {
        this.usedMem = usedMem;
    }

    protected void setAllocatedMem(M allocatedMem) {
        this.allocatedMem = allocatedMem;
    }

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
