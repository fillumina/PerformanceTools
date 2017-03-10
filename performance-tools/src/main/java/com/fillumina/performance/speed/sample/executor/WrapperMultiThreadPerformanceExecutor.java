package com.fillumina.performance.speed.sample.executor;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.infrastructure.Testable;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * It's a wrapper that will call either
 * {@link SingleTestMultiThreadPerformanceExecutor} or
 * {@link MultiThreadPerformanceExecutor} depending on the number
 * of tests submitted.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class WrapperMultiThreadPerformanceExecutor
        implements PerformanceExecutor, Serializable {
    private static final long serialVersionUID = 1L;

    private final MultiThreadPerformanceExecutor multiTestExecutor;
    private final SingleTestMultiThreadPerformanceExecutor singleTestExecutor;

    public static MultiThreadPerformanceExecutorBuilder builder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }

    /**
     * @see MultiThreadPerformanceExecutorBuilder
     */
    public WrapperMultiThreadPerformanceExecutor(final int concurrencyLevel,
            final int workerNumber,
            final long timeout,
            final TimeUnit unit) {
        this.singleTestExecutor = new SingleTestMultiThreadPerformanceExecutor(
                concurrencyLevel, workerNumber, timeout, unit);
        this.multiTestExecutor = new MultiThreadPerformanceExecutor(
                concurrencyLevel, workerNumber, timeout, unit);
    }

    @Override
    public SpeedSample executeTests(Map<String, Testable> tests,
            int[] iterations) {
        if (tests.size() == 1) {
            return singleTestExecutor.executeTests(tests, iterations);
        }
        return multiTestExecutor.executeTests(tests, iterations);
    }

}
