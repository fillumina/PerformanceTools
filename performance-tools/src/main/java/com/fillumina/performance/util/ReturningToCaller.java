package com.fillumina.performance.util;

/**
 * Allows telescopic fluent interface where it is possible to go back in the
 * call stack.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ReturningToCaller<C> {

    C end();
}
