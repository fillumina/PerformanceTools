package com.fillumina.performance.executor;

/**
 * Defines empty methods.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestable implements Testable {

    @Override
    public void setUp() {}

    @Override
    public void beforeTest(int iterations) {}
}
