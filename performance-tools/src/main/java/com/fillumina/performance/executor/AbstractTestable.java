package com.fillumina.performance.executor;

/**
 * Allows to define only the {@link #test()}.
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestable implements Testable {

    @Override
    public void setUp() {}

    @Override
    public void beforeTest(int iterations) {}
}
