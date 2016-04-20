package com.fillumina.performance.stats;

import com.fillumina.performance.sample.PerformanceSampleConsumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the consumers management (add, remove and notify).
 *
 * @author Francesco Illuminati
 */
public class PerformanceStatsProducerImpl<T extends PerformanceStatsProducerImpl<T>>
        implements PerformanceStatsProducer {

    private final List<PerformanceStatsConsumer> consumers;

    public PerformanceStatsProducerImpl() {
        this.consumers = new ArrayList<>();
    }

    public PerformanceStatsProducerImpl(final PerformanceStatsConsumer... consumers) {
        this();
        addPerformanceConsumer(consumers);
    }

    /**
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T addPerformanceConsumer(
            final PerformanceStatsConsumer... consumersArray) {
        if (consumersArray != null) {
            for (final PerformanceStatsConsumer consumer: consumersArray) {
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
            final PerformanceStatsConsumer... consumersArray) {
        if (consumersArray != null) {
            for (final PerformanceStatsConsumer consumer: consumersArray) {
                if (consumer != null) {
                    consumers.remove(consumer);
                }
            }
        }
        return (T) this;
    }

    /**
     * Passes the {@link PerformanceSample} to all {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    protected void dispatchPerformanceToConsumers(final String message,
            final PerformanceStats sample) {
        for (final PerformanceStatsConsumer consumer: consumers) {
            consumer.consume(message, sample);
        }
    }
}
