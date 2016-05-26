package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceAssertion<A>
        extends PerformanceConsumer<A>, PerformanceFormatter<A> {

    /**
     * It checks the given performance against its assertions.
     * It delegates to
     * {@link PerformanceSampleConsumer#consume(java.lang.String, com.fillumina.performance.producer.LoopPerformances) }.
     */
    void check(A performance);
}
