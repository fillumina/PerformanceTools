package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionAssertion
        extends MixedAssertion
            <StatsAssertion<SpeedStats>, StatsAssertion<MemStats>> {

    public StatsAssertion<SpeedStats> speedWithTolerance(double tolerance) {
        if (speed == null) {
            speed = AssertPerformance.withTolerance(tolerance);
        }
        return speed;
    }

    public StatsAssertion<MemStats> usedMemoryWithTolerance(double tolerance) {
        if (usedMem == null) {
            usedMem = AssertPerformance.withTolerance(tolerance);
        }
        return usedMem;
    }

    public StatsAssertion<MemStats> allocatedMemoryWithTolerance(double tolerance) {
        if (allocatedMem == null) {
            allocatedMem = AssertPerformance.withTolerance(tolerance);
        }
        return allocatedMem;
    }
}
