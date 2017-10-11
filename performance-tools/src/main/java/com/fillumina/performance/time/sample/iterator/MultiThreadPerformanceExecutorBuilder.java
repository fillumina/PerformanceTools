package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.util.Builder;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 * A mutiThreadBuilder to create a {@link DefaultPerformanceTimer}
 * based on a multi-threaded
 * executor {@link MultiThreadedPerformanceExcecutor}.
 *
 * @author Francesco Illuminati
 */
public class MultiThreadPerformanceExecutorBuilder
        implements Builder<DefaultPerformanceTimer> {
    private static final int UNLIMITED = -1;
    private static final Quantity<IntervalUnit> SEC_60 =
            IntervalUnit.SECONDS.quantity(60);

    private int threads = UNLIMITED;
    private int workers = 32;
    private Quantity<IntervalUnit> timeout = SEC_60;

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

    /**
     * Number of threads available in the pool (default: unlimited).
     * A thread is a processing unit that races for a free CPU to be
     * executed. Depending on the system a thread is executed for
     * about 100 ms on linux each time (timeslice).
     */
    public MultiThreadPerformanceExecutorBuilder
            setThreads(final int threads) {
        this.threads = threads;
        return this;
    }

    /** Creates as many threads as required (default). */
    public MultiThreadPerformanceExecutorBuilder
            setUnlimitedThreads() {
        this.threads = UNLIMITED;
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
    public MultiThreadPerformanceExecutorBuilder
            setTimeout(final Quantity<IntervalUnit> timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public DefaultPerformanceTimer build() {
        final PerformanceExecutor testExecutor =
                new SelectorMultiThreadPerformanceExecutor(
                        threads, workers, timeout);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildAsymmetricMultiThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new ParallelMultiThreadPerformanceExecutor(
                        threads, timeout);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildMultiThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new MultiThreadPerformanceExecutor(
                        threads, workers, timeout);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public DefaultPerformanceTimer buildSingleThreadPerformanceTimer() {
        final PerformanceExecutor testExecutor =
                new SingleTestMultiThreadPerformanceExecutor(
                        threads, workers, timeout);
        return new DefaultPerformanceTimer(testExecutor);
    }

    public MultiThreadPerformanceExecutor
            buildMultiThreadPerformanceExecutor() {
        return new MultiThreadPerformanceExecutor(threads,
                workers, timeout);
    }

    /** Use only if testing one test. */
    public SingleTestMultiThreadPerformanceExecutor
            buildSingleTestMultiThreadPerformanceExecutor() {
        return new SingleTestMultiThreadPerformanceExecutor(threads,
                workers, timeout);
    }
}
