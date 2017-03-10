package com.fillumina.performance.infrastructure;

/**
 * Defines empty methods.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestable extends Drain implements Testable {

    /** {@inheritDoc} */
    @Override
    public void setUp() {}

    /** {@inheritDoc} */
    @Override
    public void onBeforeSample(int iterations) {}
}
