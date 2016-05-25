package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;


/**
 * A {@link PerformanceProducer} contains none or some
 * {@link PerformanceSampleConsumer}s that it notifies about the performances it
 * collects.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceProducer<A,T>
        extends TestContainer<T> {

    PerformanceProducer<A,T> setName(ComposedName name);

    PerformanceProducer<A,T> addPerformanceConsumer(
            final PerformanceConsumer<A> consumers);

    PerformanceProducer<A,T> addPerformanceConsumerIf(boolean condition,
            final PerformanceConsumer<A> consumers);

    PerformanceProducer<A,T> addPerformanceConsumer(
            final Iterable<? extends PerformanceConsumer<A>> consumers);

    PerformanceProducer<A,T> addPerformanceConsumerIf(boolean condition,
            final Iterable<? extends PerformanceConsumer<A>> consumers);

    PerformanceProducer<A,T> removePerformanceConsumer(
            final PerformanceConsumer<A> consumers);

    PerformanceProducer<A,T> resetTests();

    PerformanceProducer<A,T> resetConsumers();

    PerformanceProducer<A,T> performGarbageCollection();

    PerformanceHolder<A> execute();
}
