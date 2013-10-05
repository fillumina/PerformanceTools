package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametersContainer <P> {

    ParametersContainer<P> addParameter(final String name, final P object);
}
