package com.fillumina.performance.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerAggregator<T>
        implements Consumer<T> {

    private final List<Consumer<? super T>> list =
            new CopyOnWriteArrayList<>();

    @SafeVarargs
    public ConsumerAggregator(Consumer<? super T>... consumers) {
        addAll(consumers);
    }

    public ConsumerAggregator(
            Collection<Consumer<? super T>> consumers) {
        for (Consumer<? super T> c : consumers) {
            addAll(c);
        }
    }

    public ConsumerAggregator<T> add(Consumer<? super T> consumer) {
        list.add(consumer);
        return this;
    }

    @SafeVarargs
    public final ConsumerAggregator<T> addAll(
            Consumer<? super T>... consumers) {
        for (Consumer<? super T> pc : consumers) {
            list.add(pc);
        }
        return this;
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

}
