package com.fillumina.performance.template;

import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.NullPerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleConsumer;
import com.fillumina.performance.sample.PerformanceSampleProducer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.TestContainer;
import com.fillumina.performance.sample.suite.AbstractParametrizedInstrumenterSuite;
import com.fillumina.performance.sample.viewer.StringTableSampleViewer;
import com.fillumina.performance.stats.NullPerformanceStatsConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;

/**
 * Template with some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTemplate<T,P> {
    private final PerformanceAssertion assertion =
            AssertPerformance.withTolerance(10); //TODO fixed to 10??

    private final TestConfigurator configuration = new TestConfigurator();

    public AbstractPerformanceTemplate() {
    }

    /**
     * Executes the test without any output.
     * This method name starts with test so that it's automatically executed by
     * old JUnit versions (previous than 4.x).
     */
    public void executeWithoutOutput() {
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
        executePerformanceTest(StringTableSampleViewer.INSTANCE,
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
    public void onAfterExecution(final PerformanceStats stats) {}

    /**
     * Defines assertions on tests.
     * <pre>
 assertion.withPercentageTolerance(1)
      .assertPercentage(<b>TEST_NAME</b>).sameAs(100);
     * </pre>
     */
    public abstract void addAssertions(final PerformanceAssertion assertion);

    protected abstract AbstractParametrizedInstrumenterSuite<?,T,P> getSuite();

    /** Override to set up a different default configuration. */
    protected void initConfiguration(TestConfigurator configuration) {}

    @SuppressWarnings("unchecked")
    public void executePerformanceTest(
            final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer) {

        initConfiguration(configuration);
        config(configuration);

        PerformanceTimer<T> producer = (PerformanceTimer<T>)
                    configuration.createPerformanceTimer();

        AbstractParametrizedInstrumenterSuite<?,T,P> suite = getSuite();
        if (suite != null) {
            addOtherData(suite);
            suite.instrument(producer);
            producer = suite;
        }

        addTests(producer);
        addAssertions(assertion);

        final AutoProgressionPerformanceInstrumenter pe =
                createPerformanceExecutor(producer, configuration,
                        iterationConsumer, resultConsumer);

        final PerformanceStats stats = pe.execute()
                .use(assertion)
                .getPerformanceStats();

        onAfterExecution(stats);
    }

    /**
     * Override to provide a
     * {@link InstrumentablePerformanceExecutor}.
     */
    private AutoProgressionPerformanceInstrumenter createPerformanceExecutor(
            final PerformanceSampleProducer producer,
            final TestConfigurator configuration,
            final PerformanceSampleConsumer iterationConsumer,
            final PerformanceStatsConsumer resultConsumer) {

        configuration.setPerformanceSampleConsumer(iterationConsumer);
        AutoProgressionPerformanceInstrumenter pe =
                configuration.create(producer);
        pe.addPerformanceConsumer(resultConsumer);

        return pe;
    }

    protected abstract void addOtherData(
            AbstractParametrizedInstrumenterSuite<?, T, P> suite);
}
