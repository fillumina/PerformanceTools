package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertion {
    private StatsAssertion<MixedAssertion, SpeedStats> speed;
    private StatsAssertion<MixedAssertion, MemStats> usedMem;
    private StatsAssertion<MixedAssertion, MemStats> allocatedMem;

    public StatsAssertion<MixedAssertion, SpeedStats> speedWithTolerance(
            Ratio tolerance) {
        if (speed == null) {
            setSpeed(new AssertStats<MixedAssertion, SpeedStats>(this)
                    .setTolerance(tolerance));
        }
        return speed;
    }

    public StatsAssertion<MixedAssertion, MemStats> usedMemoryWithTolerance(
            Ratio tolerance) {
        if (usedMem == null) {
            setUsedMem(new AssertStats<MixedAssertion, MemStats>(this)
                    .setTolerance(tolerance));
        }
        return usedMem;
    }

    public StatsAssertion<MixedAssertion, MemStats> allocatedMemoryWithTolerance(
            Ratio tolerance) {
        if (allocatedMem == null) {
            setAllocatedMem(new AssertStats<MixedAssertion, MemStats>(this)
                    .setTolerance(tolerance));
        }
        return allocatedMem;
    }

    protected void setSpeed(
            StatsAssertion<MixedAssertion, SpeedStats> speed) {
        this.speed = speed;
    }

    protected void setUsedMem(
            StatsAssertion<MixedAssertion, MemStats> usedMem) {
        this.usedMem = usedMem;
    }

    protected void setAllocatedMem(
            StatsAssertion<MixedAssertion, MemStats> allocatedMem) {
        this.allocatedMem = allocatedMem;
    }

    StatsAssertion<MixedAssertion, SpeedStats> getSpeedAssertions() {
        return speed;
    }

    StatsAssertion<MixedAssertion, MemStats> getUsedMemoryAssertions() {
        return usedMem;
    }

    StatsAssertion<MixedAssertion, MemStats> getAllocatedMemoryAssertions() {
        return allocatedMem;
    }

}
