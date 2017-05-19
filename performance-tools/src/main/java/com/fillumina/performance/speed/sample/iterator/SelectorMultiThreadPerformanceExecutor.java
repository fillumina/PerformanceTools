package com.fillumina.performance.speed.sample.iterator;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * It's a selector that will call either
 * {@link SingleTestMultiThreadPerformanceExecutor} or
 * {@link MultiThreadPerformanceExecutor} depending on the number
 * of tests submitted or {@link AsymmetricMultiThreadPerformanceExecutor} if
 * there is any {@link AsymmetricTestable} test defined.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
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
        this.asymmetricExecutor = new AsymmetricMultiThreadPerformanceExecutor(
                concurrencyLevel, timeout, unit);
    }

    @Override
    public SpeedSample executeTests(LinkedMap<TName, Runnable> tests,
            int[] iterations) {
        if (tests.entrySet().iterator().next() instanceof AsymmetricTestable) {
            return asymmetricExecutor.executeTests(tests, iterations);
        }
        if (tests.size() == 1) {
            return singleTestExecutor.executeTests(tests, iterations);
        }
        return multiTestExecutor.executeTests(tests, iterations);
    }

}
