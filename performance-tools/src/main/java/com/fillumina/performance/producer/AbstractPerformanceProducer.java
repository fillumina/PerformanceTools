package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the consumers management (add, remove and call).
 *
 * @author Francesco Illuminati
 */
public class AbstractPerformanceProducer<T extends AbstractPerformanceProducer<T>>
        implements Serializable, PerformanceProducer, PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    private final List<PerformanceConsumer> consumers;

    public AbstractPerformanceProducer() {
        this.consumers = new ArrayList<>();
    }

    public AbstractPerformanceProducer(final PerformanceConsumer... consumers) {
        this.consumers = new ArrayList<>();
        addPerformanceConsumer(consumers);
    }

    /**
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T addPerformanceConsumer(
            final PerformanceConsumer... consumersArray) {
        if (consumersArray != null) {
            for (final PerformanceConsumer consumer: consumersArray) {
                if (consumer != null) {
                    consumers.add(consumer);
                }
            }
        }
        return (T) this;
    }

    /**
     * A {@code null} argument and {@code null} array's elements are ignored.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T removePerformanceConsumer(
            final PerformanceConsumer... consumersArray) {
        if (consumersArray != null) {
            for (final PerformanceConsumer consumer: consumersArray) {
                if (consumer != null) {
                    consumers.remove(consumer);
                }
            }
        }
        return (T) this;
    }

    /**
     * Passes the {@link LoopPerformances} to all {@link PerformanceConsumer}s
     * in the same order they were added.
     */
    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        for (final PerformanceConsumer consumer: consumers) {
            consumer.consume(message, loopPerformances);
        }
    }
}
