package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceFormatter<A> {

    String toString(A performance);

    String toString(String title, A performance);
}
