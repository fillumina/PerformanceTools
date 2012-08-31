package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceTimer
        extends AbstractPerformanceProducer<PerformanceTimer>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Map<String, Runnable> tests = new LinkedHashMap<>();

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

    public abstract LoopPerformancesHolder iterate(final int iterations);

    protected void initTests() {
        for (Runnable runnable: tests.values()) {
            if (runnable instanceof InitializableRunnable) {
                ((InitializableRunnable)runnable).init();
            }
        }
    }

}
