package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutor;
import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutorBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceTimerBuilder {

    public static PerformanceTimer createSingleThread() {
        return new PerformanceTimer(new SingleThreadPerformanceTestExecutor());
    }

    /**
     * Create a {@link PerformanceTimer} with a multi threaded executor.
     * Each single test will be executed by its own in a multi threaded
     * environment where each thread will operate on the same test instance (so
     * take extra care about thread safety). The results tend to be less accurate
     * than those of a single thread.
     */
    public static PerformanceTimer createMultiThread() {
        return new PerformanceTimer(
                new MultiThreadPerformanceTestExecutorBuilder()
                .setConcurrencyLevel(32)
                .setTimeout(60, TimeUnit.SECONDS)
                .build());
    }

    public static class MultiThreadBuilder {
        final MultiThreadPerformanceTestExecutorBuilder delegate;

        public MultiThreadBuilder(
                final MultiThreadPerformanceTestExecutorBuilder delegate) {
            this.delegate = delegate;
        }

        public MultiThreadBuilder setTimeout(int timeout, TimeUnit unit) {
            delegate.setTimeout(timeout, unit);
            return this;
        }

        public MultiThreadPerformanceTestExecutorBuilder
                setConcurrencyLevel(int concurrencyLevel) {
            return delegate.setConcurrencyLevel(concurrencyLevel);
        }

        public MultiThreadBuilder setWorkers(int workerNumber) {
            delegate.setWorkers(workerNumber);
            return this;
        }

        public MultiThreadBuilder setThreads(int concurrencyLevel) {
            delegate.setThreads(concurrencyLevel);
            return this;
        }

        public PerformanceTimer build() {
            final MultiThreadPerformanceTestExecutor testExecutor = delegate.build();
            return new PerformanceTimer(testExecutor);
        }
    }

    public static MultiThreadBuilder createAdvancedMultiThread() {
        return new MultiThreadBuilder(
                new MultiThreadPerformanceTestExecutorBuilder());
    }
}
