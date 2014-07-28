package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.TimeLimited;
import com.fillumina.performance.util.Builder;
import java.util.concurrent.TimeUnit;

/**
 * A builder to create a {@link PerformanceTimer}.
 *
 * @author Francesco Illuminati
 */
public class MultiThreadPerformanceExecutorBuilder
        implements TimeLimited, Builder<PerformanceTimer> {
    private int threads = -1;
    private int workers = 32;
    private long timeout = 60;
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * Set unlimited threads and the required number of workers.
     * <b>This setting overwrites both threads and workers!</b>
     */
    public MultiThreadPerformanceExecutorBuilder
            setConcurrencyLevel(final int concurrencyLevel) {
        setUnlimitedThreads();
        setWorkers(concurrencyLevel);
        return this;
    }

    /** Number of threads available in the pool (default: unlimited). */
    public MultiThreadPerformanceExecutorBuilder
            setThreads(final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as required (default). */
    public MultiThreadPerformanceExecutorBuilder
            setUnlimitedThreads() {
        this.threads = -1;
        return this;
    }

    /**
     * Number of workers. i.e. instances of code that race for a
     * free thread to be executed (default: 32).
     */
    public MultiThreadPerformanceExecutorBuilder
            setWorkers(final int workerNumber) {
        this.workers = workerNumber;
        return this;
    }

    /**
     * Time after which the test is aborted (default: 60 s).
     */
    @Override
    public MultiThreadPerformanceExecutorBuilder
            setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    /**
     * @return a {@link PeformanceTimer} to which it is possible to add tests
     *         directly.
     */
    @Override
    public PerformanceTimer build() {
        final PerformanceExecutor testExecutor =
                buildMultiThreadPerformanceExecutor();
        return new PerformanceTimer(testExecutor);
    }

    public MultiThreadPerformanceExecutor
            buildMultiThreadPerformanceExecutor() {
        return new MultiThreadPerformanceExecutor(threads,
                workers, timeout, unit);
    }
}
