package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.TimeLimited;
import java.util.concurrent.TimeUnit;

/**
 * A mutiThreadBuilder to create a {@link DefaultPerformanceTimer} based on a multi-threaded
 * executor {@link MultiThreadedPerformanceExcecutor}.
 *
 * @author Francesco Illuminati
 */
public class MultiThreadPerformanceExecutorBuilder
        implements TimeLimited, Builder<DefaultPerformanceTimer> {
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
     * @return a {@link com.fillumina.performance.speed.sample.DefaultPerformanceTimer}
     *          to which it is possible to add tests directly.
     */
    @Override
    public DefaultPerformanceTimer build() {
        final PerformanceExecutor testExecutor =
                new SelectorMultiThreadPerformanceExecutor(
                        threads, workers, timeout, unit);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildAsymmetricMultiThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new AsymmetricMultiThreadPerformanceExecutor(
                        threads, timeout, unit);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildMultiThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new MultiThreadPerformanceExecutor(
                        threads, workers, timeout, unit);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildSingleThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new SingleTestMultiThreadPerformanceExecutor(
                        threads, workers, timeout, unit);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public MultiThreadPerformanceExecutor
            buildMultiThreadPerformanceExecutor() {
        return new MultiThreadPerformanceExecutor(threads,
                workers, timeout, unit);
    }

    /** Use only if testing one test. */
    public SingleTestMultiThreadPerformanceExecutor
            buildSingleTestMultiThreadPerformanceExecutor() {
        return new SingleTestMultiThreadPerformanceExecutor(threads,
                workers, timeout, unit);
    }
}
