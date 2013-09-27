package com.fillumina.performance.template;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.TestsContainer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;

/**
 * Configures an auto progression performance test that will iterate over
 * tests until a required stability of result is met.
 * <p>
 * The test is performed in rounds. The first round the iteration number
 * is set to {@code baseIterations} and all the tests are repeated for
 * that iteration number of times and a measurement is taken and all this
 * is repeated for {@link sample} times. By this way the algorithm
 * will have at each round {@code sample} measurements to determine if the
 * variance between the {@code sample}s is less than the maximum standard
 * deviation allowed. If it is the test is stopped and the average results
 * of the last round is returned. This means that the test guarantee to have the
 * performances within a specified stability value.
 * If the target stability is not met then the iteration number
 * is increased (actually by a power of 10) and a new round of samples is taken.
 * <p>
 * Keep the maximum allowed standard deviation high enough to prove your point
 * (especially in unit tests that must be executed fast) and lower it
 * when you need precise results.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
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
     * Defines assertions on tests.
     * <pre>
     * assertion.setTolerancePercentage(1)
     *      .assertPercentageFor(<b>TEST_NAME</b>).sameAs(100);
     * </pre>
     */
    public abstract void addAssertions(final PerformanceAssertion assertion);

    /**
     * <pre>
     * tests.addTest("test", new Runnable() {
     *       public void run() {
     *           // test code...
     *       }
     * });
     * </pre>
     */
    public abstract void addTests(final TestsContainer<?> tests);

    /** Called at the end of the execution, useful for assertion or printout. */
    public void onAfterExecution(final LoopPerformances loopPeformances) {}

    /** This method is the best candidate in case of a unit test. */
    @Override
    public void testWithoutOutput() {
        perfInstrumenter.setPrintOutStdDeviation(false);
        super.testWithoutOutput();
    }

    @Override
    protected void executePerformanceTest(
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
