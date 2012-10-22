package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.InstrumentablePerformanceExecutor;
import com.fillumina.performance.producer.PerformanceExecutorInstrumenter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceTimer
            <T extends AbstractInstrumentablePerformanceProducer<T>
                & InstrumentablePerformanceExecutor<T>>
        extends AbstractInstrumentablePerformanceProducer<T>
        implements Serializable, InstrumentablePerformanceExecutor<T> {
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
    public T setIterations(final long iterations) {
        this.iterations = iterations;
        return (T) this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializableRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}.
     *
     * @see InitializableRunnable
     * @see ThreadLocalRunnable
     */
    @Override
    @SuppressWarnings("unchecked")
    public T addTest(final String name, final Runnable test) {
        tests.put(name, test);
        return (T) this;
    }

    /**
     * Allows to ignore a test cleanly without having to comment it out.
     */
    @SuppressWarnings("unchecked")
    public T ignoreTest(final String name,
            final Runnable test) {
        return (T) this;
    }

    /** Execute the test for the given number of iterations. */
    public LoopPerformancesHolder iterate(final int iterations) {
        setIterations(iterations);
        return execute();
    }

    protected void initTests() {
        for (Runnable runnable: tests.values()) {
            if (runnable instanceof InitializableRunnable) {
                ((InitializableRunnable)runnable).init();
            }
        }
    }

    protected Map<String, Runnable> getTests() {
        return tests;
    }
}
