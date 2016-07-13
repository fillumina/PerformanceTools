package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
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
public abstract class AbstractPerformanceTemplate<T> {

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

    /** Override to set up a different default configuration. */
    protected void initConfiguration(TestConfiguration configuration) {}

    protected abstract void executePerformanceTest(int verbosityLevel);

    protected void execute(int verbosityLevel) {
        StopWatch watch = new StopWatch();
        watch.start();
        executePerformanceTest(verbosityLevel);
        if (verbosityLevel > 0) {
            System.out.println("\n\ntotal time: " +
                    TimeFormat.TEXT.formatNanoseconds(watch.stop(), 2));
        }
    }

    protected AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final PerformanceTimer performanceTimer,
            final TestConfiguration configuration,
            int verbosity) {

        AutoProgressionPerformanceInstrumenter pe =
                configuration.getSpeed().create(performanceTimer);

        ConsoleSpeedProgressionListener progressionListener =
                new ConsoleSpeedProgressionListener(verbosity);
        pe.addSampleProgressionListener(progressionListener);
        pe.addStatsProgressionListener(progressionListener);

        return pe;
    }

    protected MemAnalyzer createMemAnalyzer(MemConsumptionExecutor executor,
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

    protected void printOutConfiguration(int verbosity,
            TestConfiguration configuration) {
        if (verbosity > 0) {
            System.out.println(configuration.toString());
            System.out.println(TableFormatter.title("EXECUTION", '='));
        }
    }

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

}
