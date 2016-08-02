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
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.formatter.TimeFormat;

/**
 * Template with some simple viewers wired in.
 *
 * @param T     fluent interface self
 * @param ST    speed stats tree
 * @param MT    mem stats tree
 * @param SA
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate
        <T,                                   /* fluent interface self */
        ST,                                   /* speed stats tree */
        MT,                                   /* memory stats tree */
        SA extends Assertion<ST>,             /* speed assertion */
        MA extends Assertion<MT>> {           /* memory assertion */

    /**
     * Prints everything out. Can be verbose.
     */
    public void executeWithFullOutput() {
        execute(3);
    }

    /**
     * Prints out statistics but not samples..
     */
    public void executeWithMediumOutput() {
        execute(2);
    }

    /**
     * Prints out only the final results.
     */
    public void executeWithMinimalOutput() {
        execute(1);
    }

    /**
     * Executes the test without any output.
     */
    public void executeWithoutOutput() {
        execute(0);
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
     * tests.addTest("test", new Runnable() {
     *       public void run() {
     *           // test code...
     *       }
     * });
     * </pre>
     */
    public abstract void addTests(final TestContainer<T> tests);

    protected void execute(int verbosityLevel) {
        StopWatch watch = new StopWatch();
        watch.start();
        executePerformanceTest(verbosityLevel);
        if (verbosityLevel > 0) {
            System.out.println("\n\ntotal time: " +
                    TimeFormat.TEXT.formatNanoseconds(watch.stop(), 2));
        }
    }

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

    public void executePerformanceTest(int verbosity) {
        TestConfiguration configuration = createAndInitConfiguration(verbosity);
        MixedAssertion<SA,MA> assertion = createAndInitAssertion();
        String testName = configuration.getTestName();

        final TreeHolder<SpeedStats, ST> speedTree = calculateSpeedStats(
                testName, configuration, assertion, verbosity);

        final TreeHolder<MemStats, MT> usedMemTree = calculateUsedMemStats(
                testName, configuration, assertion, verbosity);

        final TreeHolder<MemStats, MT> allocatedMemTree = calculateAllocatedMemStats(
                testName, configuration, assertion, verbosity);

        if (verbosity > 0) {
            System.out.println(
                new TreePrint<>(testName, assertion,
                    speedTree, usedMemTree, allocatedMemTree).toString());
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
                new UsedMemConsumptionExecutor(),
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
                new AllocatedMemConsumptionExecutor(),
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

    private MemAnalyzer createMemAnalyzer(MemConsumptionExecutor executor,
            MemConfiguration memConf,
            int verbosity,
            String memTestType) {
        MemAnalyzer analyzer = new MemAnalyzer(executor,
                memConf.getSamples(),
                memConf.getStdFilterFactor())
                .addPerformanceConsumerIf(verbosity > 1,
                        memConf.getStringGenerator().viewer())
                .addMemProgressionStatusListener(
                        new ConsoleMemProgressionListener(verbosity, memTestType));
        return analyzer;
    }

    private void printOutConfiguration(int verbosity,
            TestConfiguration configuration) {
        if (verbosity > 0) {
            System.out.println(configuration.toString());
            System.out.println(TableFormatter.title("EXECUTION", '='));
        }
    }
}
