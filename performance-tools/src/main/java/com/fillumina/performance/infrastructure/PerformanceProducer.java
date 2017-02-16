package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.ComposedName;


/**
 * A {@link PerformanceProducer} contains none or some
 * {@link PerformanceSampleConsumer}s that it notifies about the performances it
 * collects.
 *
 * @param A statistics
 * @param T test
 *
 * @author Francesco Illuminati
 */
public interface PerformanceProducer<A extends Assertable, T>
        extends TestContainer<T>, PerformanceConsumerNotifier<A> {

    /** Gives a name to the test. */
    PerformanceProducer<A,T> setName(ComposedName name);

    /**
     * Performs a {@link System#gc()} and wait the given number of
     * milliseconds (usually helps the JVM to choose to effectively perform
     * garbage collection).
     *
     * @param millis number of milliseconds to wait for the GC to take place.
     * @return this (fluent interface)
     */
    PerformanceProducer<A,T> performGarbageCollection(int millis);

    /**
     * Executes the tests.
     *
     * @return the performances collected.
     */
    PHolder<A> execute();
}
