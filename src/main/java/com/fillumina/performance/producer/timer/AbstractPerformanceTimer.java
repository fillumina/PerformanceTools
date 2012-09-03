package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.PerformanceExecutor;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceTimer
        extends AbstractPerformanceProducer<AbstractPerformanceTimer>
        implements Serializable, PerformanceExecutor<AbstractPerformanceTimer> {
    private static final long serialVersionUID = 1L;

    private final Map<String, Runnable> tests = new LinkedHashMap<>();
    private int iterations;

    public int getIterations() {
        return iterations;
    }

    @SuppressWarnings("unchecked")
    public AbstractPerformanceTimer setIterations(final int iterations) {
        this.iterations = iterations;
        return this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializingRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}.
     *
     * @see InitializingRunnable
     * @see ThreadLocalRunnable
     */
    public AbstractPerformanceTimer addTest(final String name, final Runnable test) {
        tests.put(name, test);
        return this;
    }

    /**
     * Allows to ignore a test cleanly without having to comment it out.
     */
    public AbstractPerformanceTimer ignoreTest(final String name,
            final Runnable test) {
        return this;
    }

    public <T extends PerformanceTimerInstrumenter> T
            instrumentedBy(final T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
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
