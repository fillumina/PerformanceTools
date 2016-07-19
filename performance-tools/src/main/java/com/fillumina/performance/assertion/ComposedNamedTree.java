package com.fillumina.performance.assertion;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ComposedNamedTree<E> {

    E get(ComposedName name);
}
