package com.fillumina.performance.util;

import com.fillumina.performance.assertion.Assertable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BindingConsumerAggregator
        implements BindingConsumer<Object> {

    private final List<BindingConsumer<?>> list =
            new CopyOnWriteArrayList<>();

    @SafeVarargs
    public BindingConsumerAggregator(BindingConsumer<?>... consumers) {
        addAll(consumers);
    }

    public BindingConsumerAggregator(
            Collection<BindingConsumer<?>> consumers) {
        for (BindingConsumer<?> c : consumers) {
            addAll(c);
        }
    }

    @Override
    public Class<?> getAcceptedAssertableClass() {
        return Assertable.class;
    }

    public BindingConsumerAggregator add(BindingConsumer<?> consumer) {
        list.add(consumer);
        return this;
    }

    @SafeVarargs
    public final BindingConsumerAggregator addAll(
            BindingConsumer<?>... consumers) {
        for (BindingConsumer<?> pc : consumers) {
            list.add(pc);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void accept(Object assertable) {
        if (assertable != null) {
            for (BindingConsumer<?> c : list) {
                c.consumeAssertable(assertable);
            }
        }
    }

}
