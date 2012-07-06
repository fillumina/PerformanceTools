package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class BasePerformanceProducer<T extends PerformanceProducer>
        implements PerformanceProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceConsumer consumer;
    private LoopPerformances loopPerformances;

    public BasePerformanceProducer() {
    }

    public BasePerformanceProducer(final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public T setPerformanceConsumer(final PerformanceConsumer consumer) {
        this.consumer = consumer;
        return (T) this;
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

    protected PerformanceConsumer getPerformanceConsumer() {
        return consumer;
    }

    protected LoopPerformances getLoopPerformances() {
        return loopPerformances;
    }

    public String toString(final String message) {
        return message + ":\n" + toString();
    }

    @Override
    public String toString() {
        return new StringTableViewer()
                .setPerformances(loopPerformances)
                .getTable()
                .toString();
    }

}
