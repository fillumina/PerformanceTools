package com.fillumina.performance.infrastructure;

/**
 * Manages {@link AssertableConsumer}s that will be notified for
 * available performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableConsumerNotifier {

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param consumers
     */
    AssertableConsumerNotifier addConsumer(AssertableConsumer<?> consumer);

    /**
     * Adds a {@link AssertableConsumer} that will be notified when
     * performances will be available.
     * @param condition if true adds the consumer
     * @param consumer
     */
    AssertableConsumerNotifier addConsumerIf(
            boolean condition,
            AssertableConsumer<?> consumer);

    /** Removes the given {@link PerformnaceConsumer} from the managed ones. */
    AssertableConsumerNotifier removeConsumer(AssertableConsumer<?> consumer);

    /** Clear the managed consumers collection. */
    AssertableConsumerNotifier clearConsumers();
}
