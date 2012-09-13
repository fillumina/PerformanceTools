package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author fra
 */
public class AbstractPerformanceProducer<T extends AbstractPerformanceProducer<T>>
        implements Serializable, PerformanceProducer<T>, PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    private final List<PerformanceConsumer> consumers;

    public AbstractPerformanceProducer() {
        this.consumers = new ArrayList<>();
    }

    public AbstractPerformanceProducer(final PerformanceConsumer... consumers) {
        this.consumers = new ArrayList<>(Arrays.asList(consumers));
    }

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

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        for (final PerformanceConsumer consumer: consumers) {
            consumer.consume(message, loopPerformances);
        }
    }
}
