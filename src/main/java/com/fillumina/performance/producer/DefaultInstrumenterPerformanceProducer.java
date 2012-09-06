package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public class DefaultInstrumenterPerformanceProducer
        <T extends DefaultInstrumenterPerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements PerformanceExecutorInstrumenter {

    private static final long serialVersionUID = 1L;

    private DefaultPerformanceExecutorInstrumenter<T> delegate
            = new DefaultPerformanceExecutorInstrumenter<>();

    @Override
    public DefaultInstrumenterPerformanceProducer<T> instrument(
            final PerformanceExecutor<?> performanceExecutor) {
        delegate.instrument(performanceExecutor);
        return this;
    }

    public PerformanceExecutor<?> getPerformanceExecutor() {
        return delegate.getPerformanceExecutor();
    }
}
