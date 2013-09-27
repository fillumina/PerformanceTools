package com.fillumina.performance.template;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.TestsContainer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;

/**
 * Configures an auto progression performance test.
 * For any given iteration number the test is repeated by the number of
 * samples specified (each sample consist of the nominal number of iterations).
 * Those samples will be analyzed and if the maximum standard deviation is
 * lower than the maximum value given by setup then the performance test
 * stops. This means that the test automatically adjust to match the target
 * stability. If the target stability is not met then the iteration number
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

    /** Configures the test. */
    public abstract void init(final ProgressionConfigurator config);

    public abstract void addTests(final TestsContainer<?> tests);

    public abstract void addAssertions(final PerformanceAssertion assertion);

    /** Called at the end of the execution, use to assert stuff. */
    public void onAfterExecution(final LoopPerformances loopPeformances) {}

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
