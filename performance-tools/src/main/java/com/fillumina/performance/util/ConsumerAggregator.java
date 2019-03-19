package com.fillumina.performance.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerAggregator<I extends ConsumerAggregator<?,T>, T>
        implements Consumer<T> {

    private final List<Consumer<? super T>> list =
            new CopyOnWriteArrayList<>();

    public ConsumerAggregator() {
    }

    protected List<Consumer<? super T>> getConsumers() {
        return list;
    }

    @SafeVarargs
    public ConsumerAggregator(Consumer<? super T>... consumers) {
        addAllConsumers(consumers);
    }

    public ConsumerAggregator(Collection<Consumer<? super T>> consumers) {
        for (Consumer<? super T> c : consumers) {
            addAllConsumers(c);
        }
    }

    @SuppressWarnings("unchecked")
    public I addConsumerIf(boolean condition, Consumer<? super T> consumer) {
        if (condition) {
            addConsumer(consumer);
        }
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    public I addConsumer(Consumer<? super T> consumer) {
        list.add(consumer);
        return (I) this;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final I addAllConsumers(Consumer<? super T>... consumers) {
        list.addAll(Arrays.asList(consumers));
        return (I) this;
    }

    /**
     * A {@code null} argument and {@code null} array's elements are ignored.
     */
    @SuppressWarnings("unchecked")
    public I removeConsumer(final Consumer<? super T> consumer) {
        if (consumer != null) {
            list.remove(consumer);
        }
        return (I) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void accept(T t) {
        if (t != null) {
            for (Consumer<? super T> c : list) {
                c.accept(t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        list.clear();
        return (I) this;
    }
}
