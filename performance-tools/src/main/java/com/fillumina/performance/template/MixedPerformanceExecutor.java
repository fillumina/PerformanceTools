package com.fillumina.performance.template;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.mem.stats.AllocatedMemStats;
import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.mem.stats.MemStatsProducer;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.mem.stats.UsedMemStats;
import com.fillumina.performance.mem.sample.AllocatedMemSample;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSample;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.param.ParameterizedTestProducer;
import com.fillumina.performance.param.SequencedTestProducer;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.ThroughputStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.time.stats.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.time.stats.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.time.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.time.stats.strgen.TimeStatsStringGeneratorSelector;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Viewer;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO this class should be very much like a builder w/ plugins
// TODO allows plugin
public class MixedPerformanceExecutor {

    public static final MixedPerformanceExecutor INSTANCE =
            new MixedPerformanceExecutor();

    public MixedStats<?> execute(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        StopWatch watch = new StopWatch().start();

        final MixedPrinter printer =  new MixedPrinter(
                configuration.getOutput());

        if (Verbosity.MEDIUM_OUTPUT.isLessThan(verbosity)) {
            printer.printConfiguration(configuration);
        }

        // TODO put those methods in separate external builders
        MixedAssertableHolder speedTree =
                calculateSpeedStats(configuration, verbosity);

        MixedAssertableHolder usedMemTree =
                calculateUsedMemStats(configuration, verbosity);

        MixedAssertableHolder allocatedMemTree =
                calculateAllocatedMemStats(configuration, verbosity);

        final MixedStats.Builder mixedStatsBuilder =
                configuration.getMixedStatsBuilder();

        if (speedTree != null) {
            Ratio confidence = configuration.getSpeed().getConfidence();
            mixedStatsBuilder.getStatsBuilder(AverageTimeStats.class)
                    .setStringGenerator(
                            new TimeStatsStringGeneratorSelector(confidence))
                    .setStatsHolder(speedTree.getStats(AverageTimeStats.class));

            mixedStatsBuilder.getStatsBuilder(ThroughputStats.class)
                    .setStringGenerator(
                            new TimeStatsStringGeneratorSelector(confidence))
                    .setStatsHolder(speedTree.getStats(ThroughputStats.class));
        }

        if (usedMemTree != null) {
            mixedStatsBuilder.getStatsBuilder(UsedMemStats.class)
                    .setStringGenerator(MemStatsTableStringGenerator.USED_INSTANCE)
                    .setStatsHolder(usedMemTree.getStats(UsedMemStats.class));
        }

        if (allocatedMemTree != null) {
            mixedStatsBuilder.getStatsBuilder(AllocatedMemStats.class)
                    .setStringGenerator(MemStatsTableStringGenerator.ALLOCATED_INSTANCE)
                    .setStatsHolder(allocatedMemTree.getStats(AllocatedMemStats.class));
        }

        MixedStats<?> mixedStats = mixedStatsBuilder.build();

        TestListener testListener =
                configuration.<TimeStats,MemStats>getTestListener();
        if (testListener != null) {
            testListener.notify(configuration, mixedStats);
        }

        if (Verbosity.NO_OUTPUT.isLessThan(verbosity)) {
            printer.appendResults(configuration, mixedStats, watch);
        }

        final AlertPlayer player = new AlertPlayer(configuration);
        if (mixedStats.isSomeAssertionFailed()) {
            player.playFailure();
            if (Verbosity.NO_OUTPUT.equals(verbosity) ||
                    configuration.isThrowExceptionIfFailingAssertion()) {
                StringBuilder buf = new StringBuilder();
                mixedStats.appendFailedAssertionsTo(buf);
                throw new AssertionError(buf.toString());
            }
        } else {
            player.playSuccess();
        }

        return mixedStats;
    }

    // TODO extract this method (and the others) to provide autonomous builders
    private MixedAssertableHolder calculateSpeedStats(
            MixedConfiguration config,
            Verbosity verbosity) {

        SpeedConfiguration<?> speedConfig = config.getSpeed();
        if (!speedConfig.isActive()) {
            return null;
        }

        TestConfiguration<?> testConfig = config.getTestConfig();

        Ratio confidence = config.getSpeed().getConfidence();
        ConsoleSpeedProgressionListener progressionListener =
                new ConsoleSpeedProgressionListener(verbosity, confidence);

        ConfigurableStatsProducer.Strategy strategy =
                selectStrategy(config);

        return new DefaultPerformanceTimer(
                new SelectorMultiThreadPerformanceExecutor(speedConfig))

                .instrumentedBy(new ConfigurableStatsProducer(
                                    speedConfig, strategy))

                .addSampleProgressionListener(progressionListener)
                .addStatsProgressionListener(progressionListener)

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(speedConfig))
                .instrumentedBy(new ParameterizedTestProducer(testConfig))
                .instrumentedBy(new SequencedTestProducer(testConfig))

                .setName(config.getTestName())

                .addTests(testConfig.getTests())

                .get();
    }

    private ConfigurableStatsProducer.Strategy selectStrategy(
            MixedConfiguration config) {
        SpeedConfiguration<?> speedConfig = config.getSpeed();
        final ConfigurableStatsProducer.Strategy strategy;
        int[] iterations = speedConfig.getIterations();
        if (iterations != null) {
            strategy = new FixedSamplesAndIterationsStrategy(speedConfig);
        } else {
            strategy = new IncreasingSamplesStrategy(speedConfig);
        }
        return strategy;
    }

    private static final ListFilter<Double> MOST_USED_FILTER =
            MostUsedFilter.instance();

    private MixedAssertableHolder calculateUsedMemStats(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        MemConfiguration<?> usedMemConf = configuration.getUsedMem();
        if (!usedMemConf.isActive()) {
            return null;
        }

        ListFilter<Double> filter = selectFilter(usedMemConf);

        MemStatsProducer<UsedMemStats,UsedMemSample> memAnalyzer =
                new MemStatsProducer<>(
                        new UsedMemSampleProducer(),
                        usedMemConf.getSamples(),
                        filter);

        StringGenerator<MemStats> stringGenerator =
                usedMemConf.getStringGenerator();
        if (stringGenerator != null) {
            memAnalyzer.addConsumerIf(
                    Verbosity.OUTPUT_ONLY_RESULTS.isLessThan(verbosity),
                    new Viewer<>(stringGenerator));
        }

        memAnalyzer.addMemProgressionStatusListener(
                new ConsoleMemProgressionListener(verbosity, "used"));

        return executeMem(
                memAnalyzer,
                configuration);
    }

    private MixedAssertableHolder calculateAllocatedMemStats(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        MemConfiguration<?> allocatedMem = configuration.getAllocatedMem();
        if (!allocatedMem.isActive()) {
            return null;
        }

        ListFilter<Double> filter = selectFilter(allocatedMem);

        MemStatsProducer<AllocatedMemStats,AllocatedMemSample> memAnalyzed =
                new MemStatsProducer<>(
                        new AllocatedMemSampleProducer(),
                        allocatedMem.getSamples(),
                        filter);

        StringGenerator<MemStats> stringGenerator =
                allocatedMem.getStringGenerator();
        if (stringGenerator != null) {
            memAnalyzed.addConsumerIf(
                    Verbosity.OUTPUT_ONLY_RESULTS.isLessThan(verbosity),
                    new Viewer<>(stringGenerator));
        }

        memAnalyzed.addMemProgressionStatusListener(
                new ConsoleMemProgressionListener(verbosity, "allocated"));

        return executeMem(
                memAnalyzed,
                configuration);
    }

    private MixedAssertableHolder executeMem(
            MemStatsProducer<?,?> analyzer,
            MixedConfiguration configuration) {

        TestConfiguration<?> testConfig = configuration.getTestConfig();

        return analyzer
            .instrumentedBy(new ParameterizedTestProducer(testConfig))
            .instrumentedBy(new SequencedTestProducer(testConfig))
            .setName(configuration.getTestName())
            .addTests(testConfig.getTests())
            .get();
    }

    private ListFilter<Double> selectFilter(
            MemConfiguration<?> memConf) {
        ListFilter<Double> filter;
        if (memConf.isUseMostUsedFilter()) {
            filter = MOST_USED_FILTER;
        } else {
            filter = new OutlierEliminatorFilter(memConf.getStdFilterFactor());
        }
        return filter;
    }

}
