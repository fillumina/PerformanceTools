package com.fillumina.performance.suite;

/**
 *
 * @param P parameter
 *
 * @author Francesco Illuminati
 */
public interface ParameterContainer<P> {

    ParameterContainer<P> addParameter(final String name, final P param);
}
