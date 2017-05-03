package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.TName;

/**
 * Manages performance tests.
 *
 * @param T test type
 * @author Francesco Illuminati
 */
public interface TestContainer<T> {

    TestContainer<T> ignoreTest(final String name, final T test);

    /** Ignores the test (use this instead of commenting out all the lines). */
    TestContainer<T> ignoreTest(final TName name, final T test);

    TestContainer<T> addTest(final String name, final T test);

    /** Adds a named test. */
    TestContainer<T> addTest(final TName name, final T test);

    /** Clears tests. */
    TestContainer<T> clearTests();
}
