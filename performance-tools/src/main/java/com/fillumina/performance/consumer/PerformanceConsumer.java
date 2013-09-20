package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;

/**
 * Consumes performances to accomplish various tasks such as:
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
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceConsumer {

    /**
     * Consumes a named performance.
     * <p>
     * IMPLEMENTATION NOTE: if {@link LoopPerformances#EMPTY} is
     * received nothing should be performed.
     *
     * @param message           it's the test's name or a description
     * @param loopPerformances  the code performances
     */
    void consume(final String message, final LoopPerformances loopPerformances);
}
