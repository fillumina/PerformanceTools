package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Manages {@link PerformanceConsumer}s that will be notified for
 * available performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumerNotifier<A extends Assertable> {

    /**
     * Adds a {@link PerformanceConsumer} that will be notified when
     * performances will be available.
     * @param consumers
     */
    PerformanceConsumerNotifier<A> addPerformanceConsumer(
            final PerformanceConsumer<A> consumer);

    /**
     * Adds a {@link PerformanceConsumer} that will be notified when
     * performances will be available.
     * @param condition if true adds the consumer
     * @param consumer
     */
    PerformanceConsumerNotifier<A> addPerformanceConsumerIf(boolean condition,
            final PerformanceConsumer<A> consumer);

    /** Removes the given {@link PerformnaceConsumer} from the managed ones. */
    PerformanceConsumerNotifier<A> removePerformanceConsumer(
            final PerformanceConsumer<A> consumer);

    /** Clear the managed consumers collection. */
    PerformanceConsumerNotifier<A> clearConsumers();
}
