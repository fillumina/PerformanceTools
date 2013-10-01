package com.fillumina.performance.producer.suite;

/**
 *
 * @author fra
 */
public interface ParametersContainer <P> {

    ParametersContainer<P> addParameter(final String name, final P object);
}
