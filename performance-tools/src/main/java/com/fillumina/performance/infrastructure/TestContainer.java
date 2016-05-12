package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati
 */
public interface TestContainer<T> {

    /** Ignore the test (use this instead of commenting out the line). */
    TestContainer<T> ignoreTest(final String name, final T test);

    /** Add a named test. */
    TestContainer<T> addTest(final String name, final T test);
}
