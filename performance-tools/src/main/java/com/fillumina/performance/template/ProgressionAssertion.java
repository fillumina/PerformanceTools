package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.stats.Ratio;
import java.util.ArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionAssertion
        extends MixedAssertion
            <StatsAssertion<ProgressionAssertion, SpeedStats>,
             StatsAssertion<ProgressionAssertion, MemStats>> {

    public StatsAssertion<ProgressionAssertion, SpeedStats> speedWithTolerance(
            Ratio tolerance) {
        if (speed == null) {
            speed = new AssertPerformance<>(this,
                    new ArrayList<Assertion<SpeedStats>>())
                .withTolerance(tolerance);
        }
        return speed;
    }

    public StatsAssertion<ProgressionAssertion, MemStats> usedMemoryWithTolerance(
            Ratio tolerance) {
        if (usedMem == null) {
            usedMem = new AssertPerformance<>(this,
                    new ArrayList<Assertion<MemStats>>())
                .withTolerance(tolerance);
        }
        return usedMem;
    }

    public StatsAssertion<ProgressionAssertion, MemStats> allocatedMemoryWithTolerance(
            Ratio tolerance) {
        if (allocatedMem == null) {
            allocatedMem = new AssertPerformance<>(this,
                    new ArrayList<Assertion<MemStats>>())
                .withTolerance(tolerance);
        }
        return allocatedMem;
    }
}
