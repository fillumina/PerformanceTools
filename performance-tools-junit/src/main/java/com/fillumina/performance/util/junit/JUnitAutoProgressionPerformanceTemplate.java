package com.fillumina.performance.util.junit;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;

/**
 *
 * @author fra
 */
public abstract class JUnitAutoProgressionPerformanceTemplate
        extends JUnitSimplePerformanceTemplate {

    private final PerformanceInstrumenterBuilder perfInstrumenter =
            new PerformanceInstrumenterBuilder();

    public abstract void init(final PerformanceInstrumenterBuilder builder);

    public abstract void addTests(final InstrumentablePerformanceExecutor<?> pe);

    public abstract void addAssertions(final AssertPerformance ap);

    //TODO: there should be also that stuff to leave some cpu out or to stop if there it's not a multi cpu system
//    private int threads;
//    private int workers;

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

        pe.execute().use(ap);
    }

    /**
     * Override to provide a
     * {@link InstrumentablePerformanceExecutor}.
     */
    protected InstrumentablePerformanceExecutor<?> createPerformanceExecutor(
            final PerformanceInstrumenterBuilder perfInstrumenter,
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {

        InstrumentablePerformanceExecutor<?> pe = createPerformanceTimer();

        InstrumentablePerformanceExecutor<?> instrumenter =
                    perfInstrumenter.createPerformanceExecutor();

        if (instrumenter != null) {
            pe.addPerformanceConsumer(iterationConsumer);
            instrumenter.addPerformanceConsumer(resultConsumer);
            return instrumenter;

        } else {
            pe.addPerformanceConsumer(resultConsumer);
            return pe;

        }
    }

    /**
     * Override to return a {@link PerformanceTimer} with an executor
     * other than {@link SingleThreadPerformanceTestExecutor}.
     */
    protected PerformanceTimer createPerformanceTimer() {
        return PerformanceTimerBuilder.createSingleThread();
    }
}
