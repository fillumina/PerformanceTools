package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.param.ParameterizedTestProducer;
import com.fillumina.performance.param.SequencedTestProducer;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.speed.stats.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.speed.stats.progression.FixedSamplesAndIterationsStrategy;
import com.fillumina.performance.speed.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedPerformanceExecutor {

    public static final MixedPerformanceExecutor INSTANCE =
            new MixedPerformanceExecutor();

    public MixedStats execute(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        StopWatch watch = new StopWatch().start();

        final MixedPrinter printer =  new MixedPrinter(
                configuration.getOutput());

        if (Verbosity.NO_OUTPUT.isLessThan(verbosity)) {
            printer.printConfiguration(configuration);
        }

        PHolder<SpeedStats> speedTree =
                calculateSpeedStats(configuration, verbosity);

        PHolder<MemStats> usedMemTree =
                calculateUsedMemStats(configuration, verbosity);

        PHolder<MemStats> allocatedMemTree =
                calculateAllocatedMemStats(configuration, verbosity);

        final MixedStats mixedStats = configuration.getMixedStats();

        mixedStats.<SpeedStats>getStats(MixedAssertion.SPEED)
                .setViewer(new WrapperSpeedStatsTableStringGenerator(
                        configuration.getSpeed().getConfidence()))
                .setStats(speedTree);

        mixedStats.<MemStats>getStats(MixedAssertion.USED_MEM)
                .setViewer(MemStatsTableStringGenerator.USED_INSTANCE)
                .setStats(usedMemTree);

        mixedStats.<MemStats>getStats(MixedAssertion.ALLOCATED_MEM)
                .setViewer(MemStatsTableStringGenerator.ALLOCATED_INSTANCE)
                .setStats(allocatedMemTree);

        TestListener testListener =
                configuration.<SpeedStats,MemStats>getTestListener();
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

    private PHolder<SpeedStats> calculateSpeedStats(
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

        ConfigurableStatsProducer.Strategy strategy = selectStrategy(config);

        return new DefaultPerformanceTimer(
                new SelectorMultiThreadPerformanceExecutor(speedConfig))

                .instrumentedBy(
                        new ConfigurableStatsProducer(speedConfig, strategy))

                .addSampleProgressionListener(progressionListener)
                .addStatsProgressionListener(progressionListener)

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(speedConfig))
                .instrumentedBy(new ParameterizedTestProducer<>(testConfig))
                .instrumentedBy(new SequencedTestProducer<>(testConfig))

                .setName(config.getTestName())

                .addTests(testConfig.getTests())

                .execute();
    }

    private ConfigurableStatsProducer.Strategy selectStrategy(
            MixedConfiguration config) {
        SpeedConfiguration<?> speedConfig = config.getSpeed();
        final ConfigurableStatsProducer.Strategy strategy;
        int[] iterations = speedConfig.getIterations();
        if (iterations != null &&
                iterations.length == config.getTestConfig().getTests().size()) {
            strategy = new FixedSamplesAndIterationsStrategy(speedConfig);
        } else {
            strategy = new IncreasingSamplesStrategy(speedConfig);
        }
        return strategy;
    }

    private PHolder<MemStats> calculateUsedMemStats(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        MemConfiguration<?> usedMem = configuration.getUsedMem();
        if (!usedMem.isActive()) {
            return null;
        }

        MemAnalyzer usedMemAnalyzer = createMemAnalyzer(
                UsedMemConsumptionExecutor.INSTANCE,
                usedMem,
                verbosity,
                "used");

        return executeMem(
                usedMemAnalyzer,
                configuration);
    }

    private PHolder<MemStats> calculateAllocatedMemStats(
            MixedConfiguration configuration,
            Verbosity verbosity) {

        MemConfiguration<?> allocatedMem = configuration.getAllocatedMem();
        if (!allocatedMem.isActive()) {
            return null;
        }

        MemAnalyzer allocatedMemAnalyzer = createMemAnalyzer(
                AllocatedMemConsumptionExecutor.INSTANCE,
                allocatedMem,
                verbosity,
                "allocated");

        return executeMem(
                allocatedMemAnalyzer,
                configuration);
    }

    private static final ListFilter<Long, Double> MOST_USED_FILTER =
            new MostUsedFilter<>();

    private MemAnalyzer createMemAnalyzer(
            MemConsumptionExecutor executor,
            MemConfiguration<?> memConf,
            Verbosity verbosity,
            String memTestType) {

        ListFilter<Long, Double> filter;
        if (memConf.isUseMostUsedFilter()) {
            filter = MOST_USED_FILTER;
        } else {
            filter = new OutlierEliminatorFilter<>(memConf.getStdFilterFactor());
        }

        MemAnalyzer analyzer =
                new MemAnalyzer(executor, memConf.getSamples(), filter);

        StringGenerator<MemStats> stringGenerator = memConf.getStringGenerator();
        if (stringGenerator != null) {
            analyzer.addPerformanceConsumerIf(
                        Verbosity.OUTPUT_ONLY_RESULTS.isLessThan(verbosity),
                        new PerformanceViewer<>(stringGenerator));
        }

        analyzer.addMemProgressionStatusListener(
                new ConsoleMemProgressionListener(verbosity, memTestType));

        return analyzer;
    }

    private PHolder<MemStats> executeMem(
            MemAnalyzer analyzer,
            MixedConfiguration configuration) {

        TestConfiguration<?> testConfig = configuration.getTestConfig();

        return analyzer
            .instrumentedBy(new ParameterizedTestProducer<>(testConfig))
            .instrumentedBy(new SequencedTestProducer<>(testConfig))
            .setName(configuration.getTestName())
            .addTests(testConfig.getTests())
            .execute();
    }

}
