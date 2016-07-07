package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionAssertion {

    private StatsAssertion<SpeedStats> speed;
    private StatsAssertion<MemStats> used;
    private StatsAssertion<MemStats> allocated;

    public StatsAssertion<SpeedStats> speedWithTolerance(double tolerance) {
        if (speed == null) {
            speed = AssertPerformance.withTolerance(tolerance);
        }
        return speed;
    }

    public StatsAssertion<MemStats> usedMemoryWithTolerance(double tolerance) {
        if (used == null) {
            used = AssertPerformance.withTolerance(tolerance);
        }
        return used;
    }

    public StatsAssertion<MemStats> allocatedMemoryWithTolerance(double tolerance) {
        if (allocated == null) {
            allocated = AssertPerformance.withTolerance(tolerance);
        }
        return allocated;
    }

    StatsAssertion<SpeedStats> getSpeedAssertions() {
        return speed;
    }

    StatsAssertion<MemStats> getUsedMemoryAssertions() {
        return used;
    }

    StatsAssertion<MemStats> getAllocatedMemoryAssertions() {
        return allocated;
    }
}
