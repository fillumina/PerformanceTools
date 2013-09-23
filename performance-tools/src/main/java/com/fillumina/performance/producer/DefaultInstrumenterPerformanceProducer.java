package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultInstrumenterPerformanceProducer
        <T extends DefaultInstrumenterPerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements PerformanceExecutorInstrumenter {

    private static final long serialVersionUID = 1L;

    /*
     * Using a delegate to not duplicate
     * {@link DefaultPerformanceExecutorInstrumenter}'s code.
     */
    private DefaultPerformanceExecutorInstrumenter<T> delegate
            = new DefaultPerformanceExecutorInstrumenter<>();

    @Override
    public DefaultInstrumenterPerformanceProducer<T> instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        delegate.instrument(performanceExecutor);
        return this;
    }

    /**  @return the embedded {@link InstrumentablePerformanceExecutor}. */
    public InstrumentablePerformanceExecutor<?> getPerformanceExecutor() {
        return delegate.getPerformanceExecutor();
    }
}
