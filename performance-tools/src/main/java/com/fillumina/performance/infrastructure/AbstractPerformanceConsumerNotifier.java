package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.Named;
import com.fillumina.performance.util.TName;
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

    private TName name = TN.EMPTY;

    @Override
    @SuppressWarnings("unchecked")
    public I setName(TName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = TN.EMPTY.append(name);
        return (I) this;
    }

    protected TName getName() {
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
     * Passes the {@link PerformanceSample} to all
     * {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    protected void dispatchToConsumers(A assertable) {
        for (final PerformanceConsumer<A> consumer: consumers) {
            consumer.consume(assertable);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        consumers.clear();
        return (I) this;
    }

}
