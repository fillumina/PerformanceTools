package com.fillumina.performance.template;

import com.fillumina.performance.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.NullPerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.suite.AbstractParametrizedInstrumenterSuite;
import com.fillumina.performance.sample.viewer.StringTableSampleViewer;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.viewer.StringTableViewer;

/**
 * Template with some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate<T,P> {

    private final TestConfigurator perfInstrumenter =
            new TestConfigurator();

    public AbstractPerformanceTemplate() {
        perfInstrumenter.setPrintOutStdDeviation(true);
    }

    /**
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void testWithoutOutput() {
        perfInstrumenter.setPrintOutStdDeviation(false);
        executePerformanceTest(NullPerformanceSampleConsumer.INSTANCE,
                NullPerformanceStatsConsumer.INSTANCE);
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
        executePerformanceTest(NullPerformanceSampleConsumer.INSTANCE,
                StringTableViewer.INSTANCE);
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
        executePerformanceTest(StringTableSampleViewer.INSTANCE,
                StringTableViewer.INSTANCE);
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
    public abstract void init(final TestConfigurator config);

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
    public void onAfterExecution(final PerformanceStats stats) {}

    protected abstract AbstractParametrizedInstrumenterSuite<?,T,P> getSuite();

    public void executePerformanceTest(
            final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer) {
        AbstractParametrizedInstrumenterSuite<?,T,P> suite = getSuite();

        init(perfInstrumenter);

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(suite, perfInstrumenter,
                        iterationConsumer, resultConsumer);

        addTests(suite);

        addOtherData(suite);

        final PerformanceStats stats = pe.execute()
                .use(getAssertions())
                .getPerformanceStats();

        onAfterExecution(stats);
    }

    /**
     * Override to provide a
     * {@link InstrumentablePerformanceExecutor}.
     */
    private AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final AbstractParametrizedInstrumenterSuite<?,T,P> suite,
            final TestConfigurator configuration,
            final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer) {

        configuration.setPerformanceSampleConsumer(iterationConsumer);
        AutoProgressionPerformanceInstrumenter pe =
                configuration.create(suite);
        pe.addPerformanceConsumer(resultConsumer);

        return pe;
    }

    protected abstract void addOtherData(
            AbstractParametrizedInstrumenterSuite<?, T, P> suite);

    protected abstract PerformanceStatsConsumer getAssertions();
}
