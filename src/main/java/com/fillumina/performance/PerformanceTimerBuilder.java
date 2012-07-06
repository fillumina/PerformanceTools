package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceExecutor;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceTimerBuilder {

    public static PerformanceTimer createSingleThread() {
        return new PerformanceTimer(new SingleThreadPerformanceExecutor());
    }

    public static class MultiThreadBuilder {
        final MultiThreadPerformanceExecutor.Builder delegate;

        public MultiThreadBuilder(final MultiThreadPerformanceExecutor.Builder delegate) {
            this.delegate = delegate;
        }

        public MultiThreadBuilder setTimeout(int timeout, TimeUnit unit) {
            delegate.setTimeout(timeout, unit);
            return this;
        }

        public MultiThreadBuilder setWorkerNumber(int workerNumber) {
            delegate.setWorkerNumber(workerNumber);
            return this;
        }

        public MultiThreadBuilder setConcurrencyLevel(int concurrencyLevel) {
            delegate.setConcurrencyLevel(concurrencyLevel);
            return this;
        }

        public PerformanceTimer build() {
            final MultiThreadPerformanceExecutor testExecutor = delegate.build();
            return new PerformanceTimer(testExecutor);
        }
    }

    public static MultiThreadBuilder createMultiThread() {
        return new MultiThreadBuilder(
                new MultiThreadPerformanceExecutor.Builder());
    }

}
