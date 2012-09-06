package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformancesHolder;
import com.fillumina.performance.producer.AbstractInstrumentablePerformanceProducer;
import com.fillumina.performance.producer.Instrumentable;
import com.fillumina.performance.producer.PerformanceExecutor;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceTimer
            <T extends AbstractInstrumentablePerformanceProducer<T> & PerformanceExecutor<T>>
        extends AbstractInstrumentablePerformanceProducer<T>
        implements Serializable, Instrumentable, PerformanceExecutor<T> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private int iterations;

    public int getIterations() {
        return iterations;
    }

    @SuppressWarnings("unchecked")
    public T setIterations(final int iterations) {
        this.iterations = iterations;
        return (T) this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializingRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}.
     *
     * @see InitializingRunnable
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
