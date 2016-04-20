package com.fillumina.performance.sample;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceTimer
            <S extends AbstractPerformanceTimer<S,T>, T>
        implements PerformanceTimer<T> {
    private final Map<String, T> tests = new LinkedHashMap<>();
    private List<PerformanceSampleConsumer> consumers;

    @Override
    @SuppressWarnings("unchecked")
    public S addPerformanceSampleConsumer(
            PerformanceSampleConsumer... consumers) {
        if (this.consumers == null) {
            this.consumers = new ArrayList<>();
        }
        for (PerformanceSampleConsumer c : consumers) {
            this.consumers.add(c);
        }
        return (S) this;
    }

    protected void dispatchToConsumers(PerformanceSample sample) {
        if (consumers != null && !consumers.isEmpty()) {
            for (PerformanceSampleConsumer c : consumers) {
                c.consume(null, sample);
            }
        }
    }

    @Override
    public <I extends PerformanceSampleProducerInstrumenter> I instrumentedBy(
            I instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
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
    public S addTest(final String name, final T test) {
        tests.put(name, test);
        return (S) this;
    }

    /**
     * Ignore a test without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public S ignoreTest(final String name, final T test) {
        return (S) this;
    }

    protected Map<String, T> getTests() {
        return tests;
    }
}
