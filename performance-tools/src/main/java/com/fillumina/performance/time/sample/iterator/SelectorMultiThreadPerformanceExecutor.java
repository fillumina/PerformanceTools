package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.time.sample.TimeSampleBuilder;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;

/**
 * It's a selector that will call either
 * {@link SingleTestMultiThreadPerformanceExecutor} or
 * {@link MultiThreadPerformanceExecutor} depending on the number
 * of tests submitted or {@link ParallelMultiThreadPerformanceExecutor} if
 * there is any {@link ParallelTest} test defined.
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
        Quantity<IntervalUnit> getSampleTimeout();
    }

    public SelectorMultiThreadPerformanceExecutor(Configuration config) {
        this(config.getConcurrencyLevel(),
                config.getWorkerNumber(),
                config.getSampleTimeout());
    }

    public SelectorMultiThreadPerformanceExecutor(
            final int concurrencyLevel,
            final int workerNumber,
            final Quantity<IntervalUnit> timeout) {
        this.singleTestExecutor = concurrencyLevel == 1 ?
                new SingleThreadPerformanceExecutor() :
                new SingleTestMultiThreadPerformanceExecutor(
                    concurrencyLevel, workerNumber, timeout);
        this.multiTestExecutor = new MultiThreadPerformanceExecutor(
                concurrencyLevel, workerNumber, timeout);
        this.asymmetricExecutor = new ParallelMultiThreadPerformanceExecutor(
                concurrencyLevel, timeout);
    }

    @Override
    public TimeSampleBuilder executeIterations(IndexedArrayMap<TName, Runnable> tests,
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
