package com.fillumina.performance.template;

import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.template.MixedStats.SingleStats;

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
        private final MixedStats mixedStats;

        public MixedHolder(MixedStats mixedStats) {
            this.mixedStats = mixedStats;
        }

        public SingleStats<SpeedStats> speed() {
            return mixedStats.getStats(MixedAssertion.SPEED);
        }

        public SingleStats<MemStats> usedMem() {
            return mixedStats.getStats(MixedAssertion.USED_MEM);
        }

        public SingleStats<MemStats> allocatedMem() {
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
        MixedStats mixedStats =
                MixedPerformanceExecutor.INSTANCE.execute(config, verbosity);
        return new MixedHolder(mixedStats);
    }
}
