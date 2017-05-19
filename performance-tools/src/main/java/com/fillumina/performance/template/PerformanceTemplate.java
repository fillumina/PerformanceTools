package com.fillumina.performance.template;

import com.fillumina.performance.assertion.StatsAssertion;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceViewer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.ConsoleMemProgressionListener;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.param.ParameterizedTestProducer;
import com.fillumina.performance.param.SequencedTestProducer;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.speed.stats.progression.ConsecutiveExecutorStatsProducer;
import com.fillumina.performance.speed.stats.progression.ConsoleSpeedProgressionListener;
import com.fillumina.performance.speed.stats.progression.IncreasingSamplesStrategy;
import com.fillumina.performance.util.PlayAlert;
import com.fillumina.performance.util.SoundUtils;
import com.fillumina.performance.util.StopWatch;
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
    public abstract void addTests(final TestContainer<Runnable> tests);

    public abstract void addAssertions(ProgressionAssertion assertions);

    /** Override to set up a different defaults. */
    protected void initConfiguration(
            Configuration<PerformanceTemplate> configuration) {}

    private MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                    StatsAssertion<ProgressionAssertion,MemStats>>
            createAndInitAssertion() {
        ProgressionAssertion assertion = new ProgressionAssertion();
        addAssertions(assertion);
        return assertion;
    }

    private void execute(int verbosity) {
        StopWatch watch = new StopWatch();
        watch.start();

        Throwable throwable = null;
        MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                StatsAssertion<ProgressionAssertion,MemStats>> assertion = null;

        PHolder<SpeedStats> speedTree = null;
        PHolder<MemStats> usedMemTree = null;
        PHolder<MemStats> allocatedMemTree = null;

        Configuration<PerformanceTemplate> configuration =
                createAndInitConfiguration();
        printOutConfiguration(verbosity, configuration);

        TestListener testListener =
                configuration.<SpeedStats,MemStats>getTestListener();

        try {
            assertion = createAndInitAssertion();
            String testName = configuration.getTestName();

            speedTree = calculateSpeedStats(
                    testName, configuration, assertion, verbosity);

            usedMemTree = calculateUsedMemStats(
                    testName, configuration, assertion, verbosity);

            allocatedMemTree = calculateAllocatedMemStats(
                    testName, configuration, assertion, verbosity);

            final Appendable appendable = configuration.getOutput();
            if (verbosity > NO_OUTPUT && appendable != null) {

                println(appendable, "");
                if (testName != null) {
                    println(appendable, TableFormatter.title("RESULTS OF '" +
                            testName + "'", '='));
                } else {
                    println(appendable, TableFormatter.title("RESULTS", '='));
                }

                println(appendable, configuration.toString());

                println(appendable, "");

//                println(appendable, PRINTER.toString(assertion,
//                            speedTree, usedMemTree, allocatedMemTree));

                println(appendable, "Performance test total time: " +
                        TimeFormat.TEXT.formatNanoseconds(watch.stop(),
                                MEDIUM_OUTPUT));
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
            String testName,
            Configuration<PerformanceTemplate> config,
            MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                    StatsAssertion<ProgressionAssertion,MemStats>> assertion,
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

                .setName(testName)

                .addTests(testConfig.getTests())

                .execute()

                .addAssertion(assertion.getSpeedAssertions())
                .checkAssertions();
    }

    private PHolder<MemStats> calculateUsedMemStats(
            String testName,
            Configuration<PerformanceTemplate> configuration,
            MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                    StatsAssertion<ProgressionAssertion,MemStats>> assertion,
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
                testName,
                assertion.getUsedMemoryAssertions(),
                usedMemAnalyzer);
    }

    private PHolder<MemStats> calculateAllocatedMemStats(
            String testName,
            Configuration<PerformanceTemplate> configuration,
            MixedAssertion<StatsAssertion<ProgressionAssertion,SpeedStats>,
                    StatsAssertion<ProgressionAssertion,MemStats>> assertion,
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
                testName,
                assertion.getAllocatedMemoryAssertions(),
                allocatedMemAnalyzer);
    }

    private static final ListFilter<Long, Double> MOST_USED_FILTER =
            new MostUsedFilter<>();

    private MemAnalyzer createMemAnalyzer(MemConsumptionExecutor executor,
            MemConfiguration<Configuration<PerformanceTemplate>> memConf,
            int verbosity,
            String memTestType) {
        ListFilter<Long, Double> filter;
        if (memConf.isUseMostUsedFilter()) {
            filter = MOST_USED_FILTER;
        } else {
            filter = new OutlierEliminatorFilter<>(memConf.getStdFilterFactor());
        }
        StringGenerator<MemStats> stringGenerator = memConf.getStringGenerator();
        MemAnalyzer analyzer = new MemAnalyzer(executor,
                    memConf.getSamples(),
                    filter)
                .addPerformanceConsumerIf(
                        stringGenerator != null && verbosity > OUTPUT_ONLY_RESULT,
                        new PerformanceViewer<>(stringGenerator))
                .addMemProgressionStatusListener(
                        new ConsoleMemProgressionListener(verbosity, memTestType));
        return analyzer;
    }

    private void printOutConfiguration(int verbosity,
            Configuration<PerformanceTemplate> configuration) {
        if (verbosity > OUTPUT_ONLY_RESULT) {
            Appendable appendable = configuration.getOutput();
            if (appendable != null) {
                println(appendable, TableFormatter.title("CONFIGURATION", '='));
                println(appendable, configuration.toString());
                //appendConfigParameters(appendable);
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

    private PHolder<MemStats> executeMem(String testName,
            StatsAssertion<ProgressionAssertion,MemStats> assertion,
            MemAnalyzer analyzer) {

        addTests(analyzer);

        return analyzer
                .setName(testName)
                .execute()
                .check(assertion);
    }
}
