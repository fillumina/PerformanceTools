package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.TimeLimited;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiThreadPerformanceTestExecutorBuilder implements TimeLimited {
    private int threads = -1;
    private int workers = 32;
    private long timeout = 60;
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * Set unlimited threads and the required number of workers.
     * <b>This setting overwrites both threads and workers!</b>
     */
    public MultiThreadPerformanceTestExecutorBuilder
            setConcurrencyLevel(final int concurrencyLevel) {
        setUnlimitedThreads();
        setWorkers(concurrencyLevel);
        return this;
    }

    /** Number of threads available in the pool (default: unlimited). */
    public MultiThreadPerformanceTestExecutorBuilder
            setThreads(final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as required (default). */
    public MultiThreadPerformanceTestExecutorBuilder
            setUnlimitedThreads() {
        this.threads = -1;
        return this;
    }

    /**
     * Number of workers. i.e. instances of code that race for a
     * free thread to be executed (default: 32).
     */
    public MultiThreadPerformanceTestExecutorBuilder
            setWorkers(final int workerNumber) {
        this.workers = workerNumber;
        return this;
    }

    /**
     * Time after which the test is aborted (default: 60 s).
     */
    @Override
    public MultiThreadPerformanceTestExecutorBuilder
            setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public MultiThreadPerformanceTestExecutor build() {
        if (workers < 0 || timeout < 0 || unit == null) {
            throw new IllegalArgumentException();
        }
        return new MultiThreadPerformanceTestExecutor(threads,
                workers, timeout, unit);
    }

    /**
     * @return a {@link PeformanceTimer} to which it is possible to add tests
     *         directly.
     */
    public PerformanceTimer buildPerformanceTimer() {
        final PerformanceTestExecutor testExecutor = build();
        return new PerformanceTimer(testExecutor);
    }
}
