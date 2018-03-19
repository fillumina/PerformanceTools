package com.fillumina.performance.template;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.generator.PerformanceGenerator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.mem.MemStatsType;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceBuilder {

    public interface Configuration
            extends AlertPlayer.Configuration, MixedConfiguration {
        void setConsole(Appendable appendable, Verbosity verbosity);
    }

    private final Configuration config;

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

        public AssertionableResult<MixedHolder> getResult(StatsType type) {
            return mixedStats.getStats(type);
        }

        public AssertionableResult<MixedHolder> avgTime() {
            return mixedStats.getStats(TimeStatsType.AVERAGE);
        }

        public AssertionableResult<MixedHolder> throughput() {
            return mixedStats.getStats(TimeStatsType.THROUGHPUT);
        }

        public AssertionableResult<MixedHolder> usedMem() {
            return mixedStats.getStats(MemStatsType.USED);
        }

        public AssertionableResult<MixedHolder> allocatedMem() {
            return mixedStats.getStats(MemStatsType.ALLOCATED);
        }

    }

    public PerformanceBuilder(Configuration config) {
        this.config = config;
    }

    public MixedHolder executeWithoutOutput() {
        return exec(System.out, Verbosity.NO_OUTPUT);
    }

    public MixedHolder executeWithResultOutput() {
        return exec(System.out, Verbosity.OUTPUT_ONLY_RESULTS);
    }

    public MixedHolder executeWithMediumOutput() {
        return exec(System.out, Verbosity.MEDIUM_OUTPUT);
    }

    public MixedHolder executeWithFullOutput() {
        return exec(System.out, Verbosity.FULL_OUTPUT);
    }

    public MixedHolder exec(Appendable appendable, Verbosity verbosity) {
        config.setConsole(appendable, verbosity);
        return execute(config);
    }

    public static MixedHolder execute(MixedConfiguration config)
            throws AssertionError {

        PerformanceBuilderListener listener =
                config.getPerformanceBuilderListener();

        listener.onConfiguration(config);

        StopWatch timer = new StopWatch();
        timer.start();

        MixedStatsHolder mixedAssertableHolder =
                PerformanceGenerator.INSTANCE.executeMixedTests(config);

        Quantity<IntervalUnit> elapsed =
                IntervalUnit.NANOSECONDS.quantity(timer.getNanosecondsSinceStart());

        MixedAssertionableResult.Builder builder =
                config.getMixedAssertionableResultBuilder();

        mixedAssertableHolder.getStatsMap().forEach( (type, holder) ->
                builder.getStatsBuilder(type).setStatsHolder(holder) );

        MixedAssertionableResult<MixedHolder> mixedResult = builder.build();

        listener.onResults(config, mixedResult, elapsed);

        return new MixedHolder(mixedResult);
    }
}
