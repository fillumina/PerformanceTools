package com.fillumina.performance.infrastructure;

/**
 * Manages performance tests.
 *
 * @author Francesco Illuminati
 */
public interface TestContainer<T> {

    /** Ignores the test (use this instead of commenting out all the lines). */
    TestContainer<T> ignoreTest(final String name, final T test);

    /** Adds a named test. */
    TestContainer<T> addTest(final String name, final T test);

    /** Clears tests. */
    TestContainer<T> clearTests();
}
