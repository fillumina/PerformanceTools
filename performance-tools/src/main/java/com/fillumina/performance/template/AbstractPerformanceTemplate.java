package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.TableFormatter;
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
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void executeWithoutOutput() {
        execute(0);
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     public static void main(final String[] args) {
         new SomePerformanceTest().executeWithMediumOutput();
     }
 ...
 </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    public void executeWithFullOutput() {
        execute(maxVerobosity());
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     public static void main(final String[] args) {
         new SomePerformanceTest().executeWithMediumOutput();
     }
 ...
 </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    public void executeWithMediumOutput() {
        execute(maxVerobosity() - 1);
    }

    /**
     * Prints out only the final result of the test without result per
     * iteration.
     */
    public void executeWithMinimalOutput() {
        execute(maxVerobosity() - 2);
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

    protected abstract int maxVerobosity();

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

        if (verbosity > 0) {
            configuration.speed().setPerformanceSampleConsumer(
                    TemplateSampleViewer.INSTANCE);
        }
        AutoProgressionPerformanceInstrumenter pe =
                configuration.speed().create(performanceTimer);

        if (verbosity > 1) {
            pe.addPerformanceConsumer(SpeedStatsTableStringGenerator.VIEWER);
        }

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
        System.out.println(TableFormatter.title("RESULTS", '='));
        for (PerformanceHolder<?> h : holders) {
            if (h != null) {
                h.print();
            }
        }
    }
}
