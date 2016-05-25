package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;

/**
 * Template with some simple viewers wired in.
 *
 * @param A is the performance artifact returned
 * @param T is the type of test executed
 * @param P is the tyep of the parameter
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate<A,T> {

    /**
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void executeWithoutOutput() {
        executePerformanceTest(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                NullPerformanceConsumer.<PerformanceStats>instance());
    }

    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    public void executeWithIntermediateOutput() {
        executePerformanceTest(
                NullPerformanceConsumer.<PerformanceSample>instance(),
                StringTableStatsViewer.INSTANCE);
    }
    /**
     * Use in {@code main()}:
     * <pre><code>
     *     public static void main(final String[] args) {
     *         new SomePerformanceTest().executeWithIntermediateOutput();
     *     }
     * ...
     * </code></pre>
     * Produces output even for intermediate steps. It can be verbose.
     */
    public void executeWithFullOutput() {
        executePerformanceTest(StringCsvSampleViewer.INSTANCE,
                StringTableStatsViewer.INSTANCE);
    }

    /**
     * Prints out only the final result of the test without result per
     * iteration.
     */
    public void executeWithOutput() {
        executeWithIntermediateOutput();
    }

    /**
     * Configures the test. Please note that {@code TestConfigurator}
     * has some sensible defaults.
     * <pre>
     * config.setBaseIterations(1_000)
     *       .setMaxStandardDeviation(5);
     * </pre>
     */
    public abstract void config(final TestConfigurator configuration);

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

    /** Called at the end of the execution, useful for assertion or printout. */
    public void onAfterExecution(final A stats) {}

    /** Override to set up a different default configuration. */
    protected void initConfiguration(TestConfigurator configuration) {}

    protected abstract void executePerformanceTest(
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer);

    protected AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final PerformanceTimer performanceTimer,
            final TestConfigurator configuration,
            final PerformanceConsumer<PerformanceSample> iterationConsumer,
            final PerformanceConsumer<PerformanceStats> resultConsumer) {

        configuration.setPerformanceSampleConsumer(iterationConsumer);
        AutoProgressionPerformanceInstrumenter pe =
                configuration.create(performanceTimer);
        pe.addPerformanceConsumer(resultConsumer);

        return pe;
    }

}
