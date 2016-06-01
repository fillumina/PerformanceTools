package com.fillumina.performance.sample;

/**
 * Defines empty methods.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestable implements Testable {

    /** @inheritJavaDoc */
    @Override
    public void setUp() {}

    /** @inheritJavaDoc */
    @Override
    public void onBeforeSample(int iterations) {}
}
