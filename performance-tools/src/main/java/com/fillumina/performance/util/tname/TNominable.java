package com.fillumina.performance.util.tname;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TNominable<I extends TNominable<I>> {

    I setName(TName name);
}
