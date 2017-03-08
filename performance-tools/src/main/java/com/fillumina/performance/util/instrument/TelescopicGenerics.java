package com.fillumina.performance.util.instrument;

/**
 * The classes implementing this interface use themselves as generic parameter.
 * i.e. {@code List<String> -> List<List<String> -> List<List<List<String>>>}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TelescopicGenerics<T extends TelescopicGenerics<T>> {

}
