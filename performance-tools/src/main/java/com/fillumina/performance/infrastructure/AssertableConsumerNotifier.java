package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;

/**
 * Manages {@link AssertableConsumer}s that will be notified for
 * available performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableConsumerNotifier<A extends Assertable> {

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param consumers
     */
    AssertableConsumerNotifier<A> addConsumer(
            final AssertableConsumer<A> consumer);

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param condition if true adds the consumer
     * @param consumer
     */
    AssertableConsumerNotifier<A> addConsumerIf(boolean condition,
            final AssertableConsumer<A> consumer);

    /** Removes the given {@link PerformnaceConsumer} from the managed ones. */
    AssertableConsumerNotifier<A> removeConsumer(
            final AssertableConsumer<A> consumer);

    /** Clear the managed consumers collection. */
    AssertableConsumerNotifier<A> clearConsumers();
}
