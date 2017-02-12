package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestListener {
//
//    /** @return true throws the actual exception, false will silent it. */
//    <S extends AssertableMultiStats,       /* speed stats tree */
//     M extends AssertableMultiStats,       /* memory stats tree */
//     SA extends Assertion<S>,              /* speed assertion */
//     MA extends Assertion<M>>              /* memory assertion */
            boolean notify(
                TestConfiguration config,
                MixedAssertion<?, ?> assertion,
                PerformanceHolder<SpeedStats> speedStats,
                PerformanceHolder<MemStats> usedMemStats,
                PerformanceHolder<MemStats> allocatedMemStats,
                Throwable exception);
}
