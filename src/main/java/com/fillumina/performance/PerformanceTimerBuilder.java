package com.fillumina.performance;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceTimerBuilder {

    public static PerformanceTimer createSingleThread() {
        return new PerformanceTimer(new SingleThreadPerformanceTestExecutor());
    }

    public static class MultiThreadBuilder {
        final MultiThreadPerformanceTestExecutor.Builder delegate;

        public MultiThreadBuilder(final MultiThreadPerformanceTestExecutor.Builder delegate) {
            this.delegate = delegate;
        }

        public MultiThreadBuilder setTimeout(int timeout, TimeUnit unit) {
            delegate.setTimeout(timeout, unit);
            return this;
        }

        public MultiThreadBuilder setTaskNumber(int taskNumber) {
            delegate.setTaskNumber(taskNumber);
            return this;
        }

        public MultiThreadBuilder setConcurrencyLevel(int concurrencyLevel) {
            delegate.setConcurrencyLevel(concurrencyLevel);
            return this;
        }

        public PerformanceTimer build() {
            final MultiThreadPerformanceTestExecutor testExecutor = delegate.build();
            return new PerformanceTimer(testExecutor);
        }
    }

    public static MultiThreadBuilder createMultiThread() {
        return new MultiThreadBuilder(
                new MultiThreadPerformanceTestExecutor.Builder());
    }

}
