package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.LoopPerformancesHolder;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Skeleton for performance timer executors. It's separated from
 * {@link PerformanceTimer} to better allow testing.
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceTimer
            <T extends AbstractPerformanceTimer<T>>
        extends AbstractInstrumentablePerformanceProducer<T>
        implements Serializable, InstrumentablePerformanceExecutor<T>,
            IterationSettable<T> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Testable> tests = new LinkedHashMap<>();
    private long iterations;

    /**
     * Gets how many times each test is repeated in order to get
     * a more accurate result.
     */
    public long getIterations() {
        return iterations;
    }

    /**
     * Sets how many times each test is repeated in order to get
     * a more accurate result.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T setIterations(final long iterations) {
        this.iterations = iterations;
        return (T) this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializingRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}, if you need to avoid dead code
     * elimination try {@link RunnableSink}.
     *
     * @see InitializingRunnable
     * @see ThreadLocalRunnable
     * @see RunnableSink
     */
    @Override
    @SuppressWarnings("unchecked")
    public T addTest(final String name, final Testable test) {
        tests.put(name, test);
        return (T) this;
    }

    /**
     * Ignore a test without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T ignoreTest(final String name, final Testable test) {
        return (T) this;
    }

    /** Executes the test for the given number of iterations as a warmup. */
    @SuppressWarnings("unchecked")
    public T warmup(final int iterations) {
        setIterations(iterations);
        warmup();
        return (T) this;
    }

    /**
     * Executes the test for the given number of iterations and returns
     * the statistics about it. This method is mainly used directly by
     * clients while instrumenters would prefer to use
     * {@link #execute() }.
     */
    public LoopPerformancesHolder iterate(final int iterations) {
        setIterations(iterations);
        return execute();
    }

    protected void initTests() {
        for (Testable testable: tests.values()) {
            testable.setUp();
        }
    }

    protected Map<String, Testable> getTests() {
        return tests;
    }
}
