package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractPerformanceConsumerNotifier
            <I extends AbstractPerformanceConsumerNotifier<I,A>, A>
        implements PerformanceConsumerNotifier<A>, Named {

    private final List<PerformanceConsumer<A>> consumers =
            new CopyOnWriteArrayList<>();

    private ComposedName name = ComposedName.EMPTY;

    /** @inheritJavaDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I setName(ComposedName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = ComposedName.create(name);
        return (I) this;
    }

    protected ComposedName getName() {
        return name;
    }

    /** @inheritJavaDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumerIf(boolean condition,
            Iterable<? extends PerformanceConsumer<A>> consumers) {
        if (condition) {
            addPerformanceConsumer(consumers);
        }
        return (I) this;
    }

    /** @inheritJavaDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I addPerformanceConsumer(
            Iterable<? extends PerformanceConsumer<A>> consumers) {
        for (PerformanceConsumer<A> c : consumers) {
            addPerformanceConsumer(c);
        }
        return (I) this;
    }

    /** @inheritJavaDoc */
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

    /** @inheritJavaDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I resetConsumers() {
        consumers.clear();
        return (I) this;
    }

}
