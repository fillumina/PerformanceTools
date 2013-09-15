package com.fillumina.performance.util.junit;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.TestsContainer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitAutoProgressionPerformanceTemplate
        extends JUnitSimplePerformanceTemplate {

    private final ProgressionConfigurator perfInstrumenter =
            new ProgressionConfigurator();

    public abstract void init(final ProgressionConfigurator config);

    public abstract void addTests(final TestsContainer<?> tests);

    public abstract void addAssertions(final PerformanceAssertion assertion);

    /** Called at the end of the execution, use to assert stuff. */
    public void onAfterExecution(final LoopPerformances loopPeformances) {}

    @Override
    public void testWithOutput() {
        perfInstrumenter.setPrintOutStdDeviation(true);
        super.testWithOutput();
    }

    @Override
    public void test(final PerformanceConsumer iterationConsumer,
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
            final ProgressionConfigurator perfInstrumenter,
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        AutoProgressionPerformanceInstrumenter pe =
                    perfInstrumenter.create();

        pe.addPerformanceConsumer(resultConsumer);

        return pe;
    }
}
