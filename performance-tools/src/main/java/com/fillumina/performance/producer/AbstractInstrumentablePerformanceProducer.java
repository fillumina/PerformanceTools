package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractInstrumentablePerformanceProducer
        <T extends AbstractInstrumentablePerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements InstrumentablePerformanceExecutor<T> {
    private static final long serialVersionUID = 1L;

    /**
     * An instrumenter uses the {@link PerformanceExecutorInstrumenter} to
     * perform its tests using a specific pattern. For example an instrumenter
     * could execute a battery of tests and watch for variations in the
     * results continuing the tests until the variations stabilize under
     * a specified threshold.
     */
    @Override
    public <K extends PerformanceExecutorInstrumenter> K
            instrumentedBy(final K instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
