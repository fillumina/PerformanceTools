package com.fillumina.performance.util;

/**
 * Allows reentrant fluent interface where it is possible to go back in the
 * call stack.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ReentrantFluidInterface<C> {

    C end();
}
