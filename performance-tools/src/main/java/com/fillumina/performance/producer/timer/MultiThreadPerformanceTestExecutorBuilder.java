package com.fillumina.performance.producer.timer;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class MultiThreadPerformanceTestExecutorBuilder {
    private int concurrencyLevel = -1;
    private int workerNumber;
    private int timeout = 5;
    private TimeUnit unit = TimeUnit.SECONDS;

    /** Number of threads to use */
    public MultiThreadPerformanceTestExecutorBuilder
            setConcurrencyLevel(final int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    /** Creates as many threads as required */
    public MultiThreadPerformanceTestExecutorBuilder
            setUnlimitedConcurrencyLevel() {
        this.concurrencyLevel = -1;
        return this;
    }

    /**
     * Number of workers. i.e. instances of code that race for a
     * free thread to be executed.
     */
    public MultiThreadPerformanceTestExecutorBuilder
            setWorkerNumber(final int workerNumber) {
        this.workerNumber = workerNumber;
        return this;
    }

    public MultiThreadPerformanceTestExecutorBuilder
            setTimeout(final int timeout,
            final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public MultiThreadPerformanceTestExecutor build() {
        if (workerNumber < 0 || timeout < 0 || unit == null) {
            throw new IllegalArgumentException();
    }
        return new MultiThreadPerformanceTestExecutor(concurrencyLevel,
                workerNumber, timeout, unit);
    }

}
