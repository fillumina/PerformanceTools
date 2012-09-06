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

    private Map<String, LoopPerformances> resultLoopPerformance =
            new LinkedHashMap<>();

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

    public T use(final PerformanceConsumer consumer) {
        for (Map.Entry<String, LoopPerformances> entry: resultLoopPerformance.entrySet()) {
            final String name = entry.getKey();
            final LoopPerformances lp = entry.getValue();

            consumer.consume(name, lp);
        }
        return (T) this;
    }

    protected void addTestLoopPerformances(
            final String name,
            final LoopPerformances loopPerformances) {
        resultLoopPerformance.put(name, loopPerformances);
    }
}
