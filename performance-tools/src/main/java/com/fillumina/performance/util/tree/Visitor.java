package com.fillumina.performance.util.tree;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Visitor<T> {

    /** @return true to stop visiting. */
    boolean visit(T tree);
}
