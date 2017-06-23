package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * It's a selector that will call either
 * {@link SingleTestMultiThreadPerformanceExecutor} or
 * {@link MultiThreadPerformanceExecutor} depending on the number
 * of tests submitted or {@link ParallelMultiThreadPerformanceExecutor} if
 * there is any {@link ParallelTest} test defined.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO generalize this class?
public class SelectorMultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final PerformanceExecutor multiTestExecutor;
    private final PerformanceExecutor singleTestExecutor;
    private final PerformanceExecutor asymmetricExecutor;

    public interface Configuration {
        int getConcurrencyLevel();
        int getWorkerNumber();
        long getTimeoutValue();
        TimeUnit getTimeoutUnit();
    }

    public SelectorMultiThreadPerformanceExecutor(Configuration config) {
        this(config.getConcurrencyLevel(),
                config.getWorkerNumber(),
                config.getTimeoutValue(),
                config.getTimeoutUnit());
    }

    public SelectorMultiThreadPerformanceExecutor(
            final int concurrencyLevel,
            final int workerNumber,
            final long timeout,
            final TimeUnit unit) {
        this.singleTestExecutor = concurrencyLevel == 1 ?
                new SingleThreadPerformanceExecutor() :
                new SingleTestMultiThreadPerformanceExecutor(
                    concurrencyLevel, workerNumber, timeout, unit);
        this.multiTestExecutor = new MultiThreadPerformanceExecutor(
                concurrencyLevel, workerNumber, timeout, unit);
        this.asymmetricExecutor = new ParallelMultiThreadPerformanceExecutor(
                concurrencyLevel, timeout, unit);
    }

    @Override
    public TimeSample executeIterations(LinkedMap<TName, Runnable> tests,
            int[] iterations) {
        if (tests.values().iterator().next() instanceof ParallelTest) {
            return asymmetricExecutor.executeIterations(tests, iterations);
        }
        if (tests.size() == 1) {
            return singleTestExecutor.executeIterations(tests, iterations);
        }
        return multiTestExecutor.executeIterations(tests, iterations);
    }

}
