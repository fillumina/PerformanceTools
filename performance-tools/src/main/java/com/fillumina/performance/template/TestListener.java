package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestListener {

    /** @return true throws the actual exception, false will silent it. */
    <ST, MT, SA extends Assertion<ST>, MA extends Assertion<MT>> boolean notify(
                TestConfiguration config,
                MixedAssertion<SA, MA> assertion,
                TreeHolder<SpeedStats,ST> speedStats,
                TreeHolder<MemStats,MT> usedMemStats,
                TreeHolder<MemStats,MT> allocatedMemStats,
                Throwable exception);
}
