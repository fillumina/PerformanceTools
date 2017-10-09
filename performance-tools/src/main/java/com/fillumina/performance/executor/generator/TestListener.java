package com.fillumina.performance.executor.generator;


/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestListener {

    void notify(MixedConfiguration config, MixedAssertionableResult<?> assertion);
}
