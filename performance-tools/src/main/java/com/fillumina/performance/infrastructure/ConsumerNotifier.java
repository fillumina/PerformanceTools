package com.fillumina.performance.infrastructure;

import java.util.function.Consumer;

/**
 * Manages {@link AssertableConsumer}s that will be notified for
 * available performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ConsumerNotifier<C> {

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param consumers
     */
    ConsumerNotifier<C> addConsumer(Consumer<? super C> consumer);

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param condition if true adds the consumer
     * @param consumer
     */
    ConsumerNotifier<C> addConsumerIf(
            boolean condition, Consumer<? super C> consumer);

    /** Removes the given {@link PerformnaceConsumer} from the managed ones. */
    ConsumerNotifier<C> removeConsumer(Consumer<? super C> consumer);

    /** Clear the managed consumers collection. */
    ConsumerNotifier<C> clearConsumers();
}
