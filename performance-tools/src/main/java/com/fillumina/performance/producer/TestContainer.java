package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati
 */
public interface TestContainer {

    /**
     * The specified test will not be executed
     * (use this instead of commenting out the line).
     */
    TestContainer ignoreTest(final String name, final Runnable test);

    /**
     * Add a named test.
     */
    TestContainer addTest(final String name, final Runnable test);
}
