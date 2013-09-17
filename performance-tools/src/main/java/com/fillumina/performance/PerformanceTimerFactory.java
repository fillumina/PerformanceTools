package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutorBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;

/**
 * Static entry points to this API.
 * <p>
 * There are two ways of using this API:
 * <ul>
 * <li>Using a <i>fluid interface</i> that starts by defining the needed
 * {@link PerformanceTimer} using this static factory;</li>
 * <li>Using one of the templates defined into the {@code template} package.</li>
 * </ul>
 * The first choice allows for better customization while the second is
 * easier to implement.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimerFactory {

    /**
     * Creates a single threaded performance test.
     */
    public static PerformanceTimer createSingleThreaded() {
        return new PerformanceTimer(new SingleThreadPerformanceTestExecutor());
    }

    /**
     * Creates a {@link PerformanceTimer} with a multi threaded executor
     * using a specific builder (don't forget to call
     * {@link MultiThreadPerformanceTestExecutorBuilder#buildPerformanceTimer()}).
     * Each single test will be executed by its own in a multi threaded
     * environment where each thread operates on the same test instance (so
     * take extra care about thread safety).
     */
    public static MultiThreadPerformanceTestExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceTestExecutorBuilder();
    }
}
