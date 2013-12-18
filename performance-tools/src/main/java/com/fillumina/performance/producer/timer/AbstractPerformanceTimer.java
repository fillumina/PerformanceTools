package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceTimer
            <T extends AbstractPerformanceTimer<T>>
        extends AbstractInstrumentablePerformanceProducer<T>
        implements Serializable, InstrumentablePerformanceExecutor<T>,
            IterationSettable<T> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private long iterations;

    public long getIterations() {
        return iterations;
    }

    /**
     * How many times each test is repeated in order to get
     * a more accurate result.
     * <br />
     * This value <b>could be overwritten</b> by many of the
     * {@link com.fillumina.performance.producer.PerformanceExecutorInstrumenter}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T setIterations(final long iterations) {
        this.iterations = iterations;
        return (T) this;
    }

    /**
     * If you need to perform some setUpialization use
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
    public T addTest(final String name, final Runnable test) {
        tests.put(name, test);
        return (T) this;
    }

    /**
     * Allows to ignore a test cleanly without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T ignoreTest(final String name,
            final Runnable test) {
        return (T) this;
    }

    /** Executes the test for the given number of iterations as a warmup. */
    @SuppressWarnings("unchecked")
    public T warmup(final int iterations) {
        setIterations(iterations);
        warmup();
        return (T) this;
    }

    /** Executes the test for the given number of iterations. */
    public LoopPerformancesHolder iterate(final int iterations) {
        setIterations(iterations);
        return execute();
    }

    protected void initTests() {
        for (Runnable runnable: tests.values()) {
            if (runnable instanceof InitializingRunnable) {
                ((InitializingRunnable)runnable).setUp();
            }
        }
    }

    protected Map<String, Runnable> getTests() {
        return tests;
    }
}
