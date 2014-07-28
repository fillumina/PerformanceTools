package com.fillumina.performance.template;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.TestContainer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;

/**
 * Configures an auto progression performance test that will iterate over
 * tests until a required stability of result is met.
 * <p>
 * The test is performed in <b>rounds</b>. For each round the given number of
 * <b>{@code samples}</b> are taken each consisting on a measure of the time
 * employed by the tests repeated for a number of <b>{@code iterations}</b>.
 * If the {@samples} measures have a standard deviation over the maximum allowed
 * than the iteration number is increased and a new round is executed.
 * At the first round the iteration number
 * is set to {@code baseIterations} and it is increased in the successive rounds
 * by a power of 10.
 * <p>
 * By this way the algorithm
 * will have at each round {@code sample} measurements to determine if the
 * variance between the {@code sample}s is less than the maximum standard
 * deviation allowed. If it is the test is stopped and the
 * <b>average results</b> of the last round is returned.
 * This means that the test guarantee to return the
 * performances within a specified stability value.
 * If the target stability is not met within a specified timeout the test
 * fails.
 * <p>
 * Keep the maximum allowed standard deviation high enough to prove your point
 * (especially in unit tests that must be executed fast) and lower it
 * when you need more precise results.
 *
 * @author Francesco Illuminati
 */
public abstract class AutoProgressionPerformanceTemplate
        extends SimplePerformanceTemplate {

    private final ProgressionConfigurator perfInstrumenter =
            new ProgressionConfigurator();

    public AutoProgressionPerformanceTemplate() {
        perfInstrumenter.setPrintOutStdDeviation(true);
    }

    /**
     * Configures the test. Please note that {@code ProgressionConfigurator}
     * has some sensible defaults.
     * <pre>
     * config.setBaseIterations(1_000)
     *       .setMaxStandardDeviation(5);
     * </pre>
     */
    public abstract void init(final ProgressionConfigurator config);

    /**
     * <pre>
     * tests.addTest("test", new Runnable() {
     *       public void run() {
     *           // test code...
     *       }
     * });
     * </pre>
     */
    public abstract void addTests(final TestContainer tests);

    /**
     * Defines assertions on tests.
     * <pre>
     * assertion.withPercentageTolerance(1)
     *      .assertPercentageFor(<b>TEST_NAME</b>).sameAs(100);
     * </pre>
     */
    public abstract void addAssertions(final PerformanceAssertion assertion);

    /** Called at the end of the execution, useful for assertion or printout. */
    public void onAfterExecution(final LoopPerformances loopPeformances) {}

    /** This method is the best candidate in case of a unit test. */
    @Override
    public void testWithoutOutput() {
        perfInstrumenter.setPrintOutStdDeviation(false);
        super.testWithoutOutput();
    }

    @Override
    public void executePerformanceTest(
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        init(perfInstrumenter);

        final InstrumentablePerformanceExecutor<?> pe =
                createPerformanceExecutor(perfInstrumenter,
                        iterationConsumer, resultConsumer);

        addTests(pe);

        final AssertPerformance ap = new AssertPerformance();
        addAssertions(ap);

        final LoopPerformances lp = pe.execute().use(ap).getLoopPerformances();

        onAfterExecution(lp);
    }

    /**
     * Override to provide a
     * {@link InstrumentablePerformanceExecutor}.
     */
    protected InstrumentablePerformanceExecutor<?> createPerformanceExecutor(
            final ProgressionConfigurator configuration,
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        AutoProgressionPerformanceInstrumenter pe = configuration.create();

        pe.addPerformanceConsumer(resultConsumer);

        return pe;
    }
}
