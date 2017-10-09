package com.fillumina.performance.template;

import com.fillumina.performance.executor.generator.MixedConfiguration;
import com.fillumina.performance.executor.generator.Verbosity;
import com.fillumina.performance.executor.generator.AssertionableResult;
import com.fillumina.performance.executor.generator.MixedAssertionableResult;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.UsedMemStats;
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
     * {@link MixedAssertionableResult} is generic and doesn't know about specific tests,
 this class has them wired directly so you can easily access usedMemConfig
 or allocatedMemConfig without having to relay on strings.
     */
    public static class MixedHolder {
        private final MixedAssertionableResult<MixedHolder> mixedStats;

        public MixedHolder(MixedAssertionableResult<MixedHolder> mixedStats) {
            this.mixedStats = mixedStats;
            mixedStats.setCallBack(this);
        }

        public AssertionableResult<MixedHolder> avgTime() {
            return mixedStats.getStats(AverageTimeStats.class);
        }

        public AssertionableResult<MixedHolder> throughput() {
            return mixedStats.getStats(ThroughputStats.class);
        }

        public AssertionableResult<MixedHolder> usedMem() {
            return mixedStats.getStats(UsedMemStats.class);
        }

        public AssertionableResult<MixedHolder> allocatedMem() {
            return mixedStats.getStats(AllocatedMemStats.class);
        }

    }

    private PerformanceBuilder(MixedConfiguration config) {
        this.config = config;
    }

    public MixedHolder executeWithoutOutput() {
        return exec(Verbosity.NO_OUTPUT);
    }

    public MixedHolder executeWithFullOutput() {
        return exec(Verbosity.FULL_OUTPUT);
    }

    public MixedHolder exec(Verbosity verbosity) {
        @SuppressWarnings("unchecked")
        MixedAssertionableResult<MixedHolder> mixedStats = (MixedAssertionableResult<MixedHolder>)
                MixedPerformanceExecutor.INSTANCE.execute(config, verbosity);
        return new MixedHolder(mixedStats);
    }
}
