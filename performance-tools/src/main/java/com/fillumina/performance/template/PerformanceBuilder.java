package com.fillumina.performance.template;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.generator.PerformanceGenerator;
import com.fillumina.performance.executor.generator.Verbosity;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.UsedMemStats;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.util.StopWatch;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceBuilder {

    private final MixedConfigurationBuilder<PerformanceBuilder>.Configuration config;

    public static MixedConfigurationBuilder<PerformanceBuilder> config() {
        return new MixedConfigurationBuilder<>(
                config -> new PerformanceBuilder(config) );
    }

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

    PerformanceBuilder(
            MixedConfigurationBuilder<PerformanceBuilder>.Configuration config) {
        this.config = config;
    }

    public MixedHolder executeWithoutOutput() {
        return exec(Verbosity.NO_OUTPUT);
    }

    public MixedHolder executeWithFullOutput() {
        return exec(Verbosity.FULL_OUTPUT);
    }

    public MixedHolder exec(Verbosity verbosity) {
        StopWatch timer = new StopWatch();
        timer.start();

        MixedPrinter printer = new MixedPrinter(config.getAppendable());
        printer.printConfiguration(config);

        MixedAssertableHolder mixedHolder =
                PerformanceGenerator.INSTANCE.executeMixedTests(config);

        MixedAssertionableResult.Builder builder =
                config.getMixedAssertionableResultBuilder();
        mixedHolder.getStatsMap().forEach( (type, holder) ->
                builder.getStatsBuilder(type).setStatsHolder(holder) );

        MixedAssertionableResult<MixedHolder> mixedResult = builder.build();

        printer.appendResults(config, mixedResult, timer);

        return new MixedHolder(mixedResult);
    }
}
