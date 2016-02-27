package com.fillumina.performance.producer;

import com.fillumina.performance.producer.timer.Testable;

/**
 *
 * @author Francesco Illuminati
 */
public interface TestContainer {

    /**
     * The specified test will not be executed
     * (use this instead of commenting out the line).
     */
    TestContainer ignoreTest(final String name, final Testable test);

    /**
     * Add a named test.
     *
     * @see com.fillumina.performance.producer.timer.RunnableSink
     */
    TestContainer addTest(final String name, final Testable test);
}
