package com.fillumina.performance.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @param I self
 * @param C type of notification passed to consumers
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerNotifierImpl
            <I extends ConsumerNotifierImpl<I,C>, C>
        implements ConsumerNotifier<I,C> {

    private final List<Consumer<? super C>> consumers =
            new CopyOnWriteArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public I addConsumerIf(boolean condition, Consumer<? super C> consumer) {
        if (condition) {
            addConsumer(consumer);
        }
        return (I) this;
    }

    /**
     * {@link AssertableConsumer}s added here will be notified any time a
     * statistics is elaborated even if it is not the final one
     * (which will be finally reported).
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I addConsumer(Consumer<? super C> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
        return (I) this;
    }

    /**
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I removeConsumer(final Consumer<? super C> consumer) {
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
    @SuppressWarnings("unchecked")
    protected void dispatchToConsumers(C message) {
        if (message != null) {
            for (Consumer<? super C> c: consumers) {
                c.accept(message);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        consumers.clear();
        return (I) this;
    }
}
