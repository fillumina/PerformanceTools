package com.fillumina.performance.template;

import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceBuilder {

    private final MixedConfiguration config;

    public static MixedConfigurationBuilder<PerformanceBuilder> config() {
        return new MixedConfigurationBuilder<>(
                (config) -> { return new PerformanceBuilder(config); });
    }

    public static class MixedHolder {
        private final MixedStats<MixedHolder> mixedStats;

        public MixedHolder(MixedStats<MixedHolder> mixedStats) {
            this.mixedStats = mixedStats;
            mixedStats.setCallBack(this);
        }

        public AssertableStatsResult<MixedHolder,SpeedStats> speed() {
            return mixedStats.getStats(MixedAssertion.SPEED);
        }

        public AssertableStatsResult<MixedHolder,MemStats> usedMem() {
            return mixedStats.getStats(MixedAssertion.USED_MEM);
        }

        public AssertableStatsResult<MixedHolder,MemStats> allocatedMem() {
            return mixedStats.getStats(MixedAssertion.ALLOCATED_MEM);
        }

    }

    private PerformanceBuilder(MixedConfiguration config) {
        this.config = config;
    }

    public MixedHolder test() {
        return exec(Verbosity.NO_OUTPUT);
    }

    public MixedHolder exec() {
        return exec(Verbosity.FULL_OUTPUT);
    }

    public MixedHolder exec(Verbosity verbosity) {
        @SuppressWarnings("unchecked")
        MixedStats<MixedHolder> mixedStats = (MixedStats<MixedHolder>)
                MixedPerformanceExecutor.INSTANCE.execute(config, verbosity);
        return new MixedHolder(mixedStats);
    }
}
