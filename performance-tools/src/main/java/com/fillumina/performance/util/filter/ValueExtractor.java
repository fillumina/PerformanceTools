package com.fillumina.performance.util.filter;

/**
 *
 * @param T starting value
 * @param V value to return
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ValueExtractor<T,V> {

    V getValue(T t);
}
