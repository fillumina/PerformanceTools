package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.unit.IntervalUnit;

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
                    IntervalUnit.FORMATTER.toString(watch.stop()));
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

}
