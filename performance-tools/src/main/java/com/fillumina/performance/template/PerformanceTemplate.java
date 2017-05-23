package com.fillumina.performance.template;

import com.fillumina.performance.assertion.StatsAssertion;
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
import com.fillumina.performance.speed.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.PlayAlert;
import com.fillumina.performance.util.SoundUtils;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.formatter.TimeFormat;
import java.io.File;
import java.io.IOException;

/**
 * Template with some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class PerformanceTemplate {

    public static final int FULL_OUTPUT = 3;
    public static final int MEDIUM_OUTPUT = 2;
    public static final int OUTPUT_ONLY_RESULT = 1;
    public static final int NO_OUTPUT = 0;

    private static final MixedPrinter PRINTER = new MixedPrinter(
                    SpeedStatsTableStringGenerator.INSTANCE,
                    MemStatsTableStringGenerator.USED_INSTANCE,
                    MemStatsTableStringGenerator.ALLOCATED_INSTANCE);

    /**
     * Prints everything out. Can be verbose.
     */
    public void executeWithFullOutput() {
        execute(FULL_OUTPUT);
    }

    /**
     * Prints out statistics but not intermediate samples.
     * In parameterized and sequence parameterized templates
     */
    public void executeWithMediumOutput() {
        execute(MEDIUM_OUTPUT);
    }

    /**
     * Prints out only the final results.
     */
    public void executeReportingOnlyResults() {
        execute(OUTPUT_ONLY_RESULT);
    }

    /**
     * Executes the test without any output.
     */
    public void executeWithoutOutput() {
        execute(NO_OUTPUT);
    }

    /**
     * Configures the test. Please note that {@code TestConfiguration}
     * has some sensible defaults.
     * <pre>
     * config.setBaseIterations(1_000)
     *       .setMaxStandardDeviation(5);
     * </pre>
     */
    public abstract void config(final Configuration<PerformanceTemplate> config);

    /**
     * <pre>
     * tests.addTest("test", new Testable() {
     *       public Object test() {
     *           // test code...
     *           return result; // use as blackhole to avoid code eviction
     *       }
     * });
     * </pre>
     */
    public abstract void addTests(final TestConfiguration<?> tests);

    public abstract void addAssertions(MixedAssertion assertions);

    /** Override to set up a different defaults. */
    protected void initConfiguration(
            Configuration<PerformanceTemplate> configuration) {}

    private MixedAssertion createAndInitAssertion() {
        MixedAssertion assertion = new MixedAssertion();
        addAssertions(assertion);
        return assertion;
    }

    private void execute(int verbosity) {
        StopWatch watch = new StopWatch();
        watch.start();

        Throwable throwable = null;
        MixedAssertion assertion = null;

        PHolder<SpeedStats> speedTree = null;
        PHolder<MemStats> usedMemTree = null;
        PHolder<MemStats> allocatedMemTree = null;

        Configuration<PerformanceTemplate> configuration =
                createAndInitConfiguration();
        printOutConfiguration(verbosity, configuration);

        TestListener testListener =
                configuration.<SpeedStats,MemStats>getTestListener();

        addTests(configuration.getTestConfig());

        try {
            assertion = createAndInitAssertion();

            speedTree = calculateSpeedStats(
                    configuration, assertion, verbosity);

            usedMemTree = calculateUsedMemStats(
                    configuration, assertion, verbosity);

            allocatedMemTree = calculateAllocatedMemStats(
                    configuration, assertion, verbosity);

            final Appendable appendable = configuration.getOutput();
            if (verbosity > NO_OUTPUT && appendable != null) {
                appendResults(appendable, configuration, assertion,
                        speedTree, usedMemTree, allocatedMemTree, watch);
            }
        } catch (Throwable ex) {
            playAlert(configuration.isDefaultAudio(),
                    configuration.getErrorAudioFilename(), true);
            throwable = ex;
        }
        playAlert(configuration.isDefaultAudio(),
                configuration.getSuccessAudioFilename(), false);

        boolean throwException = true;
        if (testListener != null) {
            throwException = testListener.notify(configuration,
                assertion, speedTree, usedMemTree, allocatedMemTree, throwable);
        }

        if (throwException && throwable != null) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            throw new RuntimeException(throwable);
        }
    }

    private void appendResults(final Appendable appendable,
            Configuration<PerformanceTemplate> configuration,
            MixedAssertion assertion, PHolder<SpeedStats> speedTree,
            PHolder<MemStats> usedMemTree, PHolder<MemStats> allocatedMemTree,
            StopWatch watch) {
        println(appendable, "");

        printlnResult(appendable, configuration);

        println(appendable, configuration.toString());

        println(appendable, "");

        PRINTER.appendTo(appendable, assertion,
                speedTree, usedMemTree, allocatedMemTree);

        println(appendable, "Performance test total time: " +
                TimeFormat.TEXT.formatNanoseconds(watch.stop(),
                        MEDIUM_OUTPUT));
    }

    private void printlnResult(final Appendable appendable,
            Configuration<PerformanceTemplate> configuration) {
        TName testName = configuration.getTestName();
        if (testName == null || testName.isEmpty()) {
            println(appendable, TableFormatter.title("RESULTS", '='));
        } else {
            println(appendable, TableFormatter.title("RESULTS OF '" +
                    testName + "'", '='));
        }
    }

    private void playAlert(final boolean embeddedAudio,
            final String filename,
            final boolean error) {
        if (embeddedAudio) {
            if (error) {
                PlayAlert.error();
            } else {
                PlayAlert.success();
            }
        } else if (filename != null) {
            final File file = new File(filename);
            if (file.exists() && file.isFile()) {
                SoundUtils.play(file);
            }
        }
    }

    private Configuration<PerformanceTemplate> createAndInitConfiguration() {
        Configuration<PerformanceTemplate> configuration = new Configuration<>();
        initConfiguration(configuration);
        config(configuration);
        return configuration;
    }

    private PHolder<SpeedStats> calculateSpeedStats(
            Configuration<PerformanceTemplate> config,
            MixedAssertion assertion,
            int verbosity) {

        SpeedConfiguration<Configuration<PerformanceTemplate>> speedConfig =
                config.getSpeed();

        if (!speedConfig.isActive()) {
            return null;
        }

        TestConfiguration<Configuration<PerformanceTemplate>> testConfig =
                config.getTestConfig();

        ConsoleSpeedProgressionListener progressionListener =
                new ConsoleSpeedProgressionListener(verbosity);

        return new DefaultPerformanceTimer(
                new SelectorMultiThreadPerformanceExecutor(speedConfig))

                .instrumentedBy(
                    new ConfigurableStatsProducer(speedConfig,
                        new IncreasingSamplesStrategy(speedConfig)))

                .addSampleProgressionListener(progressionListener)
                .addStatsProgressionListener(progressionListener)

                .instrumentedBy(new ConsecutiveExecutorStatsProducer(speedConfig))
                .instrumentedBy(new ParameterizedTestProducer<>(testConfig))
                .instrumentedBy(new SequencedTestProducer<>(testConfig))

                .setName(config.getTestName())

                .addTests(testConfig.getTests())

                .execute()

                .addAssertion(assertion.getSpeedAssertions())
                .checkAssertions();
    }

    private PHolder<MemStats> calculateUsedMemStats(
            Configuration<PerformanceTemplate> configuration,
            MixedAssertion assertion,
            int verbosity) {

        MemConfiguration<Configuration<PerformanceTemplate>> usedMem =
                configuration.getUsedMem();

        if (!usedMem.isActive()) {
            return null;
        }

        MemAnalyzer usedMemAnalyzer = createMemAnalyzer(
                UsedMemConsumptionExecutor.INSTANCE,
                usedMem,
                verbosity,
                "used");

        return executeMem(
                assertion.getUsedMemoryAssertions(),
                usedMemAnalyzer,
                configuration);
    }

    private PHolder<MemStats> calculateAllocatedMemStats(
            Configuration<PerformanceTemplate> configuration,
            MixedAssertion assertion,
            int verbosity) {

        MemConfiguration<Configuration<PerformanceTemplate>> allocatedMem =
                configuration.getAllocatedMem();

        if (!allocatedMem.isActive()) {
            return null;
        }

        MemAnalyzer allocatedMemAnalyzer = createMemAnalyzer(
                AllocatedMemConsumptionExecutor.INSTANCE,
                allocatedMem,
                verbosity,
                "allocated");

        return executeMem(
                assertion.getAllocatedMemoryAssertions(),
                allocatedMemAnalyzer,
                configuration);
    }

    private static final ListFilter<Long, Double> MOST_USED_FILTER =
            new MostUsedFilter<>();

    private MemAnalyzer createMemAnalyzer(
            MemConsumptionExecutor executor,
            MemConfiguration<Configuration<PerformanceTemplate>> memConf,
            int verbosity,
            String memTestType) {

        // TODO allows to specify the filter directly
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
            analyzer.addPerformanceConsumerIf(verbosity > OUTPUT_ONLY_RESULT,
                        new PerformanceViewer<>(stringGenerator));
        }

        analyzer.addMemProgressionStatusListener(
                new ConsoleMemProgressionListener(verbosity, memTestType));
        return analyzer;
    }

    private PHolder<MemStats> executeMem(
            StatsAssertion<MixedAssertion,MemStats> assertion,
            MemAnalyzer analyzer,
            Configuration<PerformanceTemplate> configuration) {

        TestConfiguration<Configuration<PerformanceTemplate>> testConfig =
                configuration.getTestConfig();

        return analyzer
            .instrumentedBy(new ParameterizedTestProducer<>(testConfig))
            .instrumentedBy(new SequencedTestProducer<>(testConfig))
            .setName(configuration.getTestName())
            .addTests(testConfig.getTests())
            .execute()
            .check(assertion);
    }

    private void printOutConfiguration(int verbosity,
            Configuration<PerformanceTemplate> configuration) {
        if (verbosity > OUTPUT_ONLY_RESULT) {
            Appendable appendable = configuration.getOutput();
            if (appendable != null) {
                println(appendable, TableFormatter.title("CONFIGURATION", '='));
                println(appendable, configuration.toString());
                println(appendable, "");
                println(appendable, "");
                println(appendable, TableFormatter.title("EXECUTION", '='));
            }
        }
    }

    private void println(Appendable appendable, String str) {
        if (appendable != null) {
            try {
                appendable.append(str).append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
