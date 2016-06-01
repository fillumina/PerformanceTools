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
        extends TestContainer<T>, PerformanceConsumerNotifier<A> {

    /** Gives a name to the test. */
    PerformanceProducer<A,T> setName(ComposedName name);

    /** Clears the tests. */
    PerformanceProducer<A,T> clearTests();

    /** Clears the consumers. */
    PerformanceProducer<A,T> clearConsumers();

    /** Performs a {@link System#gc()} and wait the given number of
     * milliseconds (usually helps the JVM to choose to effectively perform
     * garbage collection).
     * @param millis number of milliseconds to wait for the gc to take place.
     * @return this (fluent interface)
     */
    PerformanceProducer<A,T> performGarbageCollection(int millis);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    PerformanceHolder<A> execute();
}
