package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Encapsulates the consumers management (add, remove and notify).
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceProducer
            <I extends AbstractPerformanceProducer<I,A,T>,
             A,
             T>
        implements PerformanceProducer<A,T> {

    private final Map<String, T> tests = new LinkedHashMap<>();

    private final List<PerformanceConsumer<A>> consumers =
            new CopyOnWriteArrayList<>();

    private ComposedName name = ComposedName.EMPTY;

    @Override
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = new ComposedName(name);
        return (I) this;
    }

    protected ComposedName getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumerIf(boolean condition,
            Iterable<? extends PerformanceConsumer<A>> consumers) {
        if (condition) {
            addPerformanceConsumer(consumers);
        }
        return (I) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumer(
            Iterable<? extends PerformanceConsumer<A>> consumers) {
        for (PerformanceConsumer<A> c : consumers) {
            addPerformanceConsumer(c);
        }
        return (I) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumerIf(boolean condition,
             PerformanceConsumer<A> consumer) {
        if (condition) {
            addPerformanceConsumer(consumer);
        }
        return (I) this;
    }

    /**
     * {@link PerformanceConsumer}s added here will be notified any time a
     * statistics is elaborated even if it is not the final one
     * (which will be finally reported).
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumer(PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
        return (I) this;
    }

    /**
     * A {@code null} argument and {@code null} array's elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I removePerformanceConsumer(final PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
        return (I) this;
    }

    /**
     * Passes the {@link PerformanceSample} to all {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    protected void dispatchToConsumers(final ComposedName name,
            final A stats) {
        for (final PerformanceConsumer<A> consumer: consumers) {
            consumer.consume(name, stats);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I resetTests() {
        tests.clear();
        return (I) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I resetConsumers() {
        consumers.clear();
        return (I) this;
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
    public I addTest(String name, T test) {
        tests.put(name, test);
        return (I) this;
    }

    /**
     * Ignore a test without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I ignoreTest(final String name, final T test) {
        return (I) this;
    }

    protected Map<String, T> getTests() {
        return tests;
    }
}
