package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class DefaultInstrumenterPerformanceProducer
        <T extends DefaultInstrumenterPerformanceProducer<T>>
    extends AbstractPerformanceProducer<T>
    implements PerformanceExecutorInstrumenter {

    private static final long serialVersionUID = 1L;

    // using a delegate to maintain a single point of responsibility
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
