package com.fillumina.performance.producer.suite;

/**
 *
 * @author fra
 */
public interface ParametersContainer <T extends ParametersContainer<T,P>,P> {

    T addParameter(final String name, final P object);
}
