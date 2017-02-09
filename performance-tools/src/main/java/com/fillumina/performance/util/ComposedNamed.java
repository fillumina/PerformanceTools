package com.fillumina.performance.util;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ComposedNamed<E> {

    E get(ComposedName name);
}
