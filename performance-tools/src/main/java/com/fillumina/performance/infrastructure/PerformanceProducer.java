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
        extends TestContainer<T>, PerformanceConsumerAggregator<A> {

    PerformanceProducer<A,T> setName(ComposedName name);

    PerformanceProducer<A,T> resetTests();

    PerformanceProducer<A,T> resetConsumers();

    PerformanceProducer<A,T> performGarbageCollection(int millis);

    PerformanceHolder<A> execute();
}
