package com.fillumina.performance.util;

/**
 * @param <I> self
 * @param <C> type of notification passed to consumers
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerNotifier<I extends ConsumerNotifier<I,C>, C>
        extends ConsumerAggregator<I, C>
        implements ConsumerContainer<I,C> {

    /**
     * Passes the {@link PerformanceSample} to all
     * {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    @SuppressWarnings("unchecked")
    protected void dispatchToConsumers(C message) {
        if (message != null) {
            getConsumers().forEach(c -> c.accept(message));
        }
    }
}
