package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Nameable<I> {

    // TODO change to CharSequence (it might include TName as well)
    I setName(String name);
}
