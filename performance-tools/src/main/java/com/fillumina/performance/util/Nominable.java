package com.fillumina.performance.util;

import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Nominable<I extends Nominable<I>> {

    I setName(TName name);
}
