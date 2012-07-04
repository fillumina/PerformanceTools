package com.fillumina.performance;

import com.fillumina.performance.timer.LoopPerformances;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceProducer<T extends PerformanceProducer>
        implements PerformanceProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceConsumer consumer;
    private LoopPerformances loopPerformances;

    @Override
    @SuppressWarnings("unchecked")
    public T setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        this.consumer = consumer;
        return (T) this;
    }

    @Override
    public <T extends PerformanceConsumer> T use(T consumer) {
        this.consumer = consumer;
        this.consumer.setPerformances(loopPerformances);
        return consumer;
    }

    protected void processConsumer(final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
        if (consumer != null && loopPerformances != null) {
            consumer.setPerformances(loopPerformances);
            consumer.process();
        }
    }
}
