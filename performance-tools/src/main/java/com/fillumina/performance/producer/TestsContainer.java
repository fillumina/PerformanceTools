package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestsContainer {

    /**
     * The specified test will not be executed
     * (use this instead of commenting out the line).
     */
    TestsContainer ignoreTest(final String name, final Runnable test);

    /**
     * Add a named test.
     */
    TestsContainer addTest(final String name, final Runnable test);
}
