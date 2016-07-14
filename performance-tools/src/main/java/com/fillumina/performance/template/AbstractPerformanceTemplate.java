package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StatsTree;
import com.fillumina.performance.infrastructure.StatsTree.Visitor;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.mem.sample.AllocatedMemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.formatter.TimeFormat;

/**
 * Template with some simple viewers wired in.
 *
 * @param A is the performance artifact returned
 * @param T is the type of test executed
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate
        <T, S extends Assertion<?>, M extends Assertion<?>> {

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
    public abstract void config(final TestConfiguration configuration);

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

    protected abstract MixedAssertion<S,M> createAndInitAssertion();

    protected abstract StatsTree<SpeedStats> executeSpeed(
            String testName,
            SpeedConfiguration speedConfiguration,
            S speedAssertions,
            AutoProgressionPerformanceInstrumenter progression);

    protected abstract StatsTree<MemStats> executeMem(
            String testName,
            M memoryAssertions,
            MemAnalyzer analyzer);

    public void executePerformanceTest(int verbosity) {
        TestConfiguration configuration = createAndInitConfiguration(verbosity);
        MixedAssertion<S,M> assertion = createAndInitAssertion();
        String testName = configuration.getTestName();

        final StatsTree<SpeedStats> speedTree = calculateSpeedStats(
                testName, configuration, assertion, verbosity);

        final StatsTree<MemStats> usedMemTree = calculateUsedMemStats(
                testName, configuration, assertion, verbosity);

        final StatsTree<MemStats> allocatedMemTree = calculateAllocatedMemStats(
                testName, configuration, assertion, verbosity);

        if (verbosity > 0) {
            print(testName, assertion, speedTree, usedMemTree, allocatedMemTree);
        }
    }

    private TestConfiguration createAndInitConfiguration(int verbosity) {
        TestConfiguration configuration = new TestConfiguration();
        initConfiguration(configuration);
        config(configuration);
        printOutConfiguration(verbosity, configuration);
        return configuration;
    }

    private StatsTree<SpeedStats> calculateSpeedStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<S, M> assertion,
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

    private StatsTree<MemStats> calculateUsedMemStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<S, M> assertion,
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

    private StatsTree<MemStats> calculateAllocatedMemStats(
            String testName,
            TestConfiguration configuration,
            MixedAssertion<S, M> assertion,
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

    @Deprecated
    protected void printResults(int verbosity, PerformanceHolder<?>... holders) {
        if (verbosity == 0) {
            return;
        }
        System.out.println("\n" + TableFormatter.title("RESULTS", '='));
        for (PerformanceHolder<?> h : holders) {
            if (h != null) {
                h.print();
            }
        }
    }

    @Deprecated
    protected <S,M> void printAssertions(int verbosity,
            PerformanceHolder<S> speedStats,
            PerformanceHolder<M> usedMemStats,
            PerformanceHolder<M> allocatedMemStats,
            Assertion<S> speedAssertion,
            Assertion<M> usedMemAssertion,
            Assertion<M> allocatedMemAssertion) {
        if (verbosity == 0) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(assertionToString("Speed",
                speedAssertion,
                speedStats));
        buf.append(assertionToString("Used Memory",
                usedMemAssertion,
                usedMemStats));
        buf.append(assertionToString("Allocated Memory",
                allocatedMemAssertion,
                allocatedMemStats));
        if (buf.length() != 0) {
            System.out.println("\n" + TableFormatter.title("ASSERTIONS", '=') +
                    buf.toString());
        }
    }

    @Deprecated
    private <A> String assertionToString(
            String title,
            Assertion<A> statsAssertion,
            PerformanceHolder<A> stats) {
        if (statsAssertion == null) {
            return "";
        }
        return System.lineSeparator() + title + System.lineSeparator() +
                statsAssertion.toString(stats.getPerformance());
    }

    private void print(final String testName,
            final MixedAssertion<S, M> assertion,
            final StatsTree<SpeedStats> speedStats,
            final StatsTree<MemStats> usedMemStats,
            final StatsTree<MemStats> allocatedMemStats) {

        System.out.println("");
        if (testName != null) {
            System.out.println(TableFormatter.title("RESULTS FOR " + testName, '='));
        } else {
            System.out.println(TableFormatter.title("RESULTS", '='));
        }

        StatsTree<? extends AssertableMultiStats> tree =
                calculateNotNullTree(speedStats, usedMemStats, allocatedMemStats);

        final S speedAssertions = assertion.getSpeedAssertions();
        final M usedMemoryAssertions = assertion.getUsedMemoryAssertions();
        final M allocatedMemoryAssertions = assertion.getAllocatedMemoryAssertions();

        tree.traverse(new Visitor() {
            @Override
            public void visitTitle(int level, ComposedName name) {
                System.out.println("");
                switch (level) {
                    case 0:
                        System.out.println(name);
                        break;

                    case 1:
                        System.out.println(
                                TableFormatter.title(name.toString(), '-'));
                        break;

                    case 2:
                        System.out.println(
                                TableFormatter.title(name.toString(), '='));
                        break;
                }
            }

            @Override
            public void visitStats(ComposedName name, AssertableMultiStats stats) {
                if (speedStats != null) {
                    SpeedStats ams = speedStats.getStats(name);
                    if (ams != null) {
                        SpeedStatsTableStringGenerator.VIEWER.consume(null, ams);
                        if (speedAssertions != null) {
                            for (Assertion<AssertableMultiStats> a :
                                    speedAssertions.getLeaves(name)) {
                                System.out.println(a.toString(ams));
                            }
                        }
                    }
                }
                if (usedMemStats != null) {
                    MemStats ams = usedMemStats.getStats(name);
                    if (ams != null) {
                        MemStatsTableStringGenerator.USED_INSTANCE
                                .viewer().consume(null, ams);
                        if (usedMemoryAssertions != null) {
                            for (Assertion<AssertableMultiStats> a :
                                    usedMemoryAssertions.getLeaves(name)) {
                                System.out.println(a.toString(ams));
                            }
                        }
                    }
                }
                if (allocatedMemStats != null) {
                    MemStats ams = allocatedMemStats.getStats(name);
                    if (ams != null) {
                        MemStatsTableStringGenerator.ALLOCATED_INSTANCE
                                .viewer().consume(null, ams);
                        if (allocatedMemoryAssertions != null) {
                            for (Assertion<AssertableMultiStats> a :
                                    allocatedMemoryAssertions.getLeaves(name)) {
                                System.out.println(a.toString(ams));
                            }
                        }
                    }
                }
            }
        });
    }

    private StatsTree<? extends AssertableMultiStats> calculateNotNullTree(
            StatsTree<? extends AssertableMultiStats>... trees) {
        for (StatsTree<? extends AssertableMultiStats> st : trees) {
            if (st != null) {
                return st;
            }
        }
        throw new RuntimeException("no stats found!");
    }

}
