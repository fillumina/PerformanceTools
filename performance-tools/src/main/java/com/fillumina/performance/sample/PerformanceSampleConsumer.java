package com.fillumina.performance.sample;

/**
 * Consumes a named {@link PerformanceSample}.
 *
 * @author Francesco Illuminati
 */
public interface PerformanceSampleConsumer {

    /**
     * Consumes a {@link PerformanceSample} with an associated message.
     *
     * @param testName           it's the test's name or description
     *                          possibly {@code null}.
     * @param sample            the sample. {@code null} should be
     *                          expected with a do-nothing semantic.
     */
    void consume(final String testName, final PerformanceSample sample);
}
