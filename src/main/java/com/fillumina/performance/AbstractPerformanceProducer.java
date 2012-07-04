package com.fillumina.performance;

import com.fillumina.performance.timer.LoopPerformances;
import com.fillumina.performance.view.StringTableViewer;
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
        processConsumer(null, loopPerformances);
    }

    protected void processConsumer(final String message,
            final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
        if (consumer != null && loopPerformances != null) {
            consumer.setPerformances(loopPerformances);
            consumer.setMessage(message);
            consumer.process();
        }
    }

    public String toString(final String message) {
        return message + ":\n" + toString();
    }

    @Override
    public String toString() {
        return use(new StringTableViewer()).getTable().toString();
    }

}
