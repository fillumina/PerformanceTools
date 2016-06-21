package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.NullPerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.ComposedName;

/**
 * Template with some simple viewers wired in.
 *
 * @param A is the performance artifact returned
 * @param T is the type of test executed
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate<A,T> {
    private PerformanceConsumer<PerformanceSample> sampleConsumer =
            NullPerformanceConsumer.<PerformanceSample>instance();
    private PerformanceConsumer<SpeedStats> statsConsumer =
            NullPerformanceConsumer.<SpeedStats>instance();

    /**
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void executeWithoutOutput() {
        executePerformanceTest(false);
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
        this.statsConsumer = SpeedTableStringGenerator.VIEWER;
        executePerformanceTest(true);
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
        this.sampleConsumer = SampleLineStringGenerator.VIEWER;
        this.statsConsumer = SpeedTableStringGenerator.VIEWER;
        executePerformanceTest(true);
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

    public PerformanceConsumer<PerformanceSample> getSampleConsumer() {
        return sampleConsumer;
    }

    public PerformanceConsumer<SpeedStats> getStatsConsumer() {
        return statsConsumer;
    }

    protected abstract void executePerformanceTest(boolean printout);

    protected AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final PerformanceTimer performanceTimer,
            final TestConfigurator configuration) {

        configuration.setPerformanceSampleConsumer(getSampleConsumer());
        AutoProgressionPerformanceInstrumenter pe =
                configuration.create(performanceTimer);
        pe.addPerformanceConsumer(getStatsConsumer());

        return pe;
    }

    protected void printOutConfiguration(boolean printout,
            TestConfigurator configuration) {
        if (printout) {
            System.out.println("CONFIGURATION:\n\n" + configuration.toString());
            System.out.println("\n\nEXECUTION:\n");
        }
    }

    protected void printOutAssertion(boolean printout,
            StringGenerator<A> assertion,
            ComposedName name,
            A performance) {
        if (printout) {
            final String assertionStr = assertion.toString(name, performance);
            if (assertionStr != null && !assertionStr.isEmpty()) {
                System.out.println("ASSERTION:\n\n" + assertionStr);
            }
        }
    }
}
