package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.TreeHolder;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.Platform;
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
 * @param T     fluent interface self
 * @param ST    speed stats tree
 * @param MT    mem stats tree
 * @param SA    speed assertion
 * @param MA    memory assertion
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate
        <T,                                   /* fluent interface self */
        ST,                                   /* speed stats tree */
        MT,                                   /* memory stats tree */
        SA extends Assertion<ST>,             /* speed assertion */
        MA extends Assertion<MT>> {           /* memory assertion */

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
    public abstract void config(final TestConfiguration config);

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
    public abstract void addTests(final TestContainer<T> tests);

    /** Override to set up a different default configuration. */
    protected void initConfiguration(TestConfiguration configuration) {}

    protected abstract MixedAssertion<SA,MA> createAndInitAssertion();

    protected abstract TreeHolder<SpeedStats, ST> executeSpeed(
            String testName,
            SpeedConfiguration speedConfiguration,
            SA speedAssertions,
            AutoProgressionPerformanceInstrumenter progression);

    protected abstract TreeHolder<MemStats, MT> executeMem(
            String testName,
            MA memoryAssertions,
            MemAnalyzer analyzer);

    private void execute(int verbosity) {
        StopWatch watch = new StopWatch();
        watch.start();

        TestConfiguration configuration = createAndInitConfiguration(verbosity);

        try {
            MixedAssertion<SA,MA> assertion = createAndInitAssertion();
            String testName = configuration.getTestName();

            final TreeHolder<SpeedStats, ST> speedTree = calculateSpeedStats(
                    testName, configuration, assertion, verbosity);

            final TreeHolder<MemStats, MT> usedMemTree = calculateUsedMemStats(
                    testName, configuration, assertion, verbosity);

            final TreeHolder<MemStats, MT> allocatedMemTree = calculateAllocatedMemStats(
                    testName, configuration, assertion, verbosity);

            if (verbosity > NO_OUTPUT) {
                final Appendable appendable = configuration.getOutput();

                println(appendable, "");
                if (testName != null) {
                    println(appendable, TableFormatter.title("RESULTS FOR '" +
                            testName + "'", '='));
                } else {
                    println(appendable, TableFormatter.title("RESULTS", '='));
                }

                println(appendable, Platform.INSTANCE.toString());
                println(appendable, "");
                println(appendable, "");

                println(appendable,
                    new TreePrint<>(assertion,
                            speedTree, usedMemTree, allocatedMemTree).toString());

                println(appendable, "Performance test total time: " +
                        TimeFormat.TEXT.formatNanoseconds(watch.stop(), MEDIUM_OUTPUT));
            }
        } catch (Throwable ex) {
            playAlert(configuration.isDefaultAudio(),
                    configuration.getErrorAudioFilename());
            throw ex;
        }
        playAlert(configuration.isDefaultAudio(),
                configuration.getSuccessAudioFilename());
    }

    private void playAlert(final boolean embeddedAudio, final String filename) {
        if (embeddedAudio) {
            PlayAlert.error();
        } else if (filename != null) {
            final File file = new File(filename);
            if (file.exists() && file.isFile()) {
                SoundUtils.play(file);
            }
        }
    }

    private TestConfiguration createAndInitConfiguration(int verbosity) {
        TestConfiguration configuration = new TestConfiguration();
        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(verbosity, configuration);
        return configuration;
    }

    private TreeHolder<SpeedStats, ST> calculateSpeedStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<SA, MA> assertion,
            int verbosity) {

        if (!configuration.getSpeed().isActive()) {
            return null;
        }
        SpeedConfiguration speedConfiguration = configuration.getSpeed();
        PerformanceTimer producer = speedConfiguration.createPerformanceTimer();
        AutoProgressionPerformanceInstrumenter progression =
                createPerformanceExecutor(producer, speedConfiguration, verbosity);

        return executeSpeed(testName,
                speedConfiguration,
                assertion.getSpeedAssertions(),
                progression);
    }

    private TreeHolder<MemStats, MT> calculateUsedMemStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<SA, MA> assertion,
            int verbosity) {
        if (!configuration.getUsedMem().isActive()) {
            return null;
        }
        MemAnalyzer usedMemAnalyzer = createMemAnalyzer(
                UsedMemConsumptionExecutor.INSTANCE,
                configuration.getUsedMem(),
                verbosity,
                "used");

        return executeMem(
                testName,
                assertion.getUsedMemoryAssertions(),
                usedMemAnalyzer);
    }

    private TreeHolder<MemStats, MT> calculateAllocatedMemStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<SA, MA> assertion,
            int verbosity) {
        if (!configuration.getAllocatedMem().isActive()) {
            return null;
        }
        MemAnalyzer allocatedMemAnalyzer = createMemAnalyzer(
                AllocatedMemConsumptionExecutor.INSTANCE,
                configuration.getAllocatedMem(),
                verbosity,
                "allocated");

        return executeMem(
                testName,
                assertion.getAllocatedMemoryAssertions(),
                allocatedMemAnalyzer);
    }

    private AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final PerformanceTimer performanceTimer,
            final SpeedConfiguration speedConfiguration,
            int verbosity) {

        AutoProgressionPerformanceInstrumenter pe =
                speedConfiguration.create(performanceTimer);

        ConsoleSpeedProgressionListener progressionListener =
                new ConsoleSpeedProgressionListener(verbosity);
        pe.addSampleProgressionListener(progressionListener);
        pe.addStatsProgressionListener(progressionListener);

        return pe;
    }

    private static final ListFilter<Long, Double> MOST_USED_FILTER =
            new MostUsedFilter<>();

    private MemAnalyzer createMemAnalyzer(MemConsumptionExecutor executor,
            MemConfiguration memConf,
            int verbosity,
            String memTestType) {
        ListFilter<Long, Double> filter;
        if (memConf.isUseMostUsedFilter()) {
            filter = MOST_USED_FILTER;
        } else {
            filter = new OutlierEliminatorFilter<>(memConf.getStdFilterFactor());
        }
        MemAnalyzer analyzer = new MemAnalyzer(executor,
                memConf.getSamples(),
                filter)
                .addPerformanceConsumerIf(verbosity > OUTPUT_ONLY_RESULT,
                        memConf.getStringGenerator().viewer())
                .addMemProgressionStatusListener(
                        new ConsoleMemProgressionListener(verbosity, memTestType));
        return analyzer;
    }

    private void printOutConfiguration(int verbosity,
            TestConfiguration configuration) {
        if (verbosity > OUTPUT_ONLY_RESULT) {
            Appendable appendable = configuration.getOutput();
            println(appendable, configuration.toString());
            println(appendable, TableFormatter.title("EXECUTION", '='));
        }
    }

    private void println(Appendable appendable, String str) {
        try {
            appendable.append(str).append(System.lineSeparator());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
