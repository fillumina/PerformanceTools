package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;

/**
 * Consumes a named {@link LoopPerformances} to accomplish various tasks such as:
 * <ul>
 * <li>
 * Assert something about the performances
 * (i.e. {@link com.fillumina.performance.consumer.assertion.AssertPerformance});
 * </li>
 * <li>
 * Print out the performances
 * (i.e. {@link com.fillumina.performance.consumer.viewer.StringTableViewer},
 * {@link com.fillumina.performance.consumer.viewer.StringCsvViewer}).
 * </li>
 * </ul>
 *
 * @author Francesco Illuminati
 */
public interface PerformanceConsumer {

    /**
     * Consumes a {@link LoopPerformances} with an associated message.
     *
     * @param message           it's the test's name or description
     *                          possibly {@code null}.
     * @param loopPerformances  the performances. {@code null} should be
     *                          expected with a do-nothing semantic.
     */
    void consume(final String message, final LoopPerformances loopPerformances);
}
