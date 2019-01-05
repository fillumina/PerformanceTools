package com.fillumina.performance.util.tname;

/**
 * The object can be named with a {@link TName}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TNominable<I extends TNominable<I>> {

    I setName(TName name);
}
