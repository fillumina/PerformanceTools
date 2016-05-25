package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumerAggregator<A> {

    PerformanceConsumerAggregator<A> addPerformanceConsumer(
            final PerformanceConsumer<A> consumers);

    PerformanceConsumerAggregator<A> addPerformanceConsumerIf(boolean condition,
            final PerformanceConsumer<A> consumers);

    PerformanceConsumerAggregator<A> addPerformanceConsumer(
            final Iterable<? extends PerformanceConsumer<A>> consumers);

    PerformanceConsumerAggregator<A> addPerformanceConsumerIf(boolean condition,
            final Iterable<? extends PerformanceConsumer<A>> consumers);

    PerformanceConsumerAggregator<A> removePerformanceConsumer(
            final PerformanceConsumer<A> consumers);
}
