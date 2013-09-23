package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public interface TestsContainer<T extends TestsContainer<T>> {

    /**
     * The specified test will not be executed
     * (use this instead of commenting out the line).
     */
    T ignoreTest(final String name, final Runnable test);

    /**
     * Add a named test.
     */
    T addTest(final String name, final Runnable test);
}
