package com.fillumina.performance.producer.suite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterContainer <P> {

    ParameterContainer<P> addParameter(final String name, final P object);
}
