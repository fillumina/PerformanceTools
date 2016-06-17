package com.fillumina.performance.speed.sample;

/**
 * Defines empty methods.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestable implements Testable {

    @Override
    public void setUp() {}

    @Override
    public void onBeforeSample(int iterations) {}
}
