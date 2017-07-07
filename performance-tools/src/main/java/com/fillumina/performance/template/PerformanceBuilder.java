package com.fillumina.performance.template;

import com.fillumina.performance.mem.AllocatedMemStats;
import com.fillumina.performance.mem.UsedMemStats;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;

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

    /**
     * {@link MixedStats} is generic and doesn't know about specific tests,
     * this class has them wired directly so you can easily access usedMem
     * or allocatedMem without having to relay on strings.
     */
    public static class MixedHolder {
        private final MixedStats<MixedHolder> mixedStats;

        public MixedHolder(MixedStats<MixedHolder> mixedStats) {
            this.mixedStats = mixedStats;
            mixedStats.setCallBack(this);
        }

        public AssertableStatsResult<MixedHolder> avgTime() {
            return mixedStats.getStats(AverageTimeStats.class);
        }

        public AssertableStatsResult<MixedHolder> throughput() {
            return mixedStats.getStats(ThroughputStats.class);
        }

        public AssertableStatsResult<MixedHolder> usedMem() {
            return mixedStats.getStats(UsedMemStats.class);
        }

        public AssertableStatsResult<MixedHolder> allocatedMem() {
            return mixedStats.getStats(AllocatedMemStats.class);
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
