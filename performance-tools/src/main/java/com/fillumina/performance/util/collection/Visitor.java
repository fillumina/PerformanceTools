package com.fillumina.performance.util.collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Visitor<T> {

    /** @return true to stop visiting. */
    boolean visit(T t);
}
