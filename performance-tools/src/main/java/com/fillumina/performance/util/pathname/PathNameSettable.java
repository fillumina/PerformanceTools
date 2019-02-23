package com.fillumina.performance.util.pathname;

/**
 * The object can be named with a {@link PathName}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PathNameSettable<I extends PathNameSettable<I>> {

    I setPathName(PathName name);
}
