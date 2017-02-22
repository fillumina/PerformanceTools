package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractPerformanceConsumerNotifier
            <I extends AbstractPerformanceConsumerNotifier<I,A>,
             A extends Assertable>
        implements PerformanceConsumerNotifier<A>, Named {

    private final List<PerformanceConsumer<A>> consumers =
            new CopyOnWriteArrayList<>();

    private ComposedName name = CName.ROOT;

    @Override
    @SuppressWarnings("unchecked")
    public I setName(ComposedName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = CName.ROOT.append(name);
        return (I) this;
    }

    protected ComposedName getName() {
        return name;
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
    protected void dispatchToConsumers(final PHolder<A> stats) {
        for (final PerformanceConsumer<A> consumer: consumers) {
            consumer.consume(stats);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        consumers.clear();
        return (I) this;
    }

}
