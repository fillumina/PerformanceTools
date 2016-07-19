package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;


/**
 * A {@link PerformanceProducer} contains none or some
 * {@link PerformanceSampleConsumer}s that it notifies about the performances it
 * collects.
 *
 * @param S the tree
 * @param A the leaf
 * @param T test
 * 
 * @author Francesco Illuminati
 */
public interface PerformanceProducer<S,A,T>
        extends TestContainer<T>, PerformanceConsumerNotifier<A> {

    /** Gives a name to the test. */
    PerformanceProducer<S,A,T> setName(ComposedName name);

    /** Performs a {@link System#gc()} and wait the given number of
     * milliseconds (usually helps the JVM to choose to effectively perform
     * garbage collection).
     * @param millis number of milliseconds to wait for the gc to take place.
     * @return this (fluent interface)
     */
    PerformanceProducer<S,A,T> performGarbageCollection(int millis);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    TreeHolder<S,A> execute();
}
