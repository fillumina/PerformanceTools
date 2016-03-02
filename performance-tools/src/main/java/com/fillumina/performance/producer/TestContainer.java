package com.fillumina.performance.producer;

import com.fillumina.performance.executor.Testable;

/**
 *
 * @author Francesco Illuminati
 */
public interface TestContainer {

    /** Ignore the test (use this instead of commenting out the line). */
    TestContainer ignoreTest(final String name, final Testable test);

    /** Add a named test. */
    TestContainer addTest(final String name, final Testable test);
}
