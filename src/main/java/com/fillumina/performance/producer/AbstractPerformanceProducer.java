package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fra
 */
public class AbstractPerformanceProducer<T extends AbstractPerformanceProducer<?>>
        implements Serializable, PerformanceProducer<T> {
    private static final long serialVersionUID = 1L;

    private final List<PerformanceConsumer> consumers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public T addPerformanceConsumer(
            final PerformanceConsumer... consumersArray) {
        if (consumersArray != null) {
            for (final PerformanceConsumer consumer: consumersArray) {
                consumers.add(consumer);
            }
        }
        return (T) this;
    }

    protected void processConsumers(final String message,
            final LoopPerformances loopPerformances) {
        for (final PerformanceConsumer consumer: consumers) {
            consumer.consume(message, loopPerformances);
        }
    }
}
