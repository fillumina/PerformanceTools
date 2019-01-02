package com.fillumina.performance.executor;

import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 * Container for tests.
 *
 * @param T test type
 * @author Francesco Illuminati
 */
public interface TestContainer<I extends TestContainer<I,T>,T> {

    IndexedHashMap<TName,T> getTests();

    /**
     * Ignores the test
     * (convenience method to avoid commenting out large code blocks).
     */
    I ignoreTest(final String name, final T test);

    /**
     * Ignores the test
     * (convenience method to avoid commenting out large code blocks).
     */
    I ignoreTest(final TName name, final T test);

    @SuppressWarnings("unchecked")
    default I clearAndAddAllTests(Map<TName,T> tests) {
        clearTests();
        addTests(tests);
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    default I clearAndAddAllTests(TestContainer<?,T> other) {
        TestContainer.this.clearAndAddAllTests(other.getTests());
        return (I) this;
    }

    /** Adds some tests. */
    I addTests(Map<TName,T> tests);

    /** Adds a single test (name generation is implementation dependant). */
    I addTest(final T test);

    /** Adds a named test. */
    I addTest(final String name, final T test);

    /** Adds a named test. */
    I addTest(final TName name, final T test);

    /** Clears tests. */
    I clearTests();
}
