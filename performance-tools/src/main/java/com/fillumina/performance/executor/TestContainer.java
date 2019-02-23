package com.fillumina.performance.executor;

import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import java.util.Map;

/**
 * Container for tests.
 *
 * @param T test type
 * @author Francesco Illuminati
 */
public interface TestContainer<I extends TestContainer<I,T>,T> {

    IndexedHashMap<PathName,T> getTests();

    /**
     * Ignores the test
     * (convenience method to avoid commenting out large code blocks).
     */
    I ignoreTest(final String name, final T test);

    /**
     * Ignores the test
     * (convenience method to avoid commenting out large code blocks).
     */
    I ignoreTest(final PathName name, final T test);

    @SuppressWarnings("unchecked")
    default I clearAndAddAllTests(Map<PathName,T> tests) {
        clearTests();
        addTests(tests);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    default I clearAndAddAllTests(TestContainer<?,T> other) {
        TestContainer.this.clearAndAddAllTests(other.getTests());
        return (I) this;
    }

    /** Adds some named tests. */
    I addTests(Map<PathName,T> tests);

    /** Adds a single test (name generation is implementation dependent). */
    I addTest(final T test);

    /** Adds a named test. */
    I addTest(final String name, final T test);

    /** Adds a named test. */
    I addTest(final PathName name, final T test);

    /** Clears tests. */
    I clearTests();
}
