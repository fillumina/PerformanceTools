package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutorBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimerBuilder {

    public static PerformanceTimer createSingleThreaded() {
        return new PerformanceTimer(new SingleThreadPerformanceTestExecutor());
    }

    /**
     * Create a {@link PerformanceTimer} with a multi threaded executor.
     * Each single test will be executed by its own in a multi threaded
     * environment where each thread operates on the same test instance (so
     * take extra care about thread safety). The results tend to be less accurate
     * than with single threaded tests.
     */
    public static MultiThreadPerformanceTestExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceTestExecutorBuilder();
    }
}
