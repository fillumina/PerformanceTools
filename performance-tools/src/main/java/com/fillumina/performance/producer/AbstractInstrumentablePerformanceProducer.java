package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public abstract class AbstractInstrumentablePerformanceProducer
        <T extends AbstractInstrumentablePerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements InstrumentablePerformanceExecutor<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public <K extends PerformanceExecutorInstrumenter> K
            instrumentedBy(final K instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
