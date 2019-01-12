package com.fillumina.performance.util;

import java.util.function.Consumer;

/**
 * Manages {@link Consumer}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ConsumerContainer<I extends ConsumerContainer<I,C>, C> {

    /**
     * Adds a consumer.
     *
     * @param consumers
     */
    I addConsumer(Consumer<? super C> consumer);

    /**
     * Adds a consumer if the condition is true.
     *
     * @param condition if true adds the consumer
     * @param consumer
     */
    I addConsumerIf(boolean condition, Consumer<? super C> consumer);

    /** Removes the given consumer from the managed ones. */
    I removeConsumer(Consumer<? super C> consumer);

    /** Clear the managed consumers. */
    I clearConsumers();
}
