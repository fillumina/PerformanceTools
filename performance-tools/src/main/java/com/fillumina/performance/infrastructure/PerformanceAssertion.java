package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceAssertion<A>
        extends PerformanceConsumer<A>, PerformanceFormatter<A> {

    void check(A performance);
}
