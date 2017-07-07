package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerAggregator
        implements AssertableConsumer<Assertable> {

    private final List<AssertableConsumer<?>> list =
            new ArrayList<>();

    public AssertableConsumerAggregator() {
    }

    @SafeVarargs
    public AssertableConsumerAggregator(AssertableConsumer<?>... consumers) {
        addAll(consumers);
    }

    public AssertableConsumerAggregator(
            Collection<AssertableConsumer<?>> consumers) {
        for (AssertableConsumer<?> c : consumers) {
            addAll(c);
        }
    }

    @Override
    public Class<Assertable> getAcceptedAssertableClass() {
        return Assertable.class;
    }

    public AssertableConsumerAggregator add(AssertableConsumer<?> consumer) {
        list.add(consumer);
        return this;
    }

    @SafeVarargs //TODO apply @SafeVarargs to other cases
    public final AssertableConsumerAggregator addAll(
            AssertableConsumer<?>... consumers) {
        for (AssertableConsumer<?> pc : consumers) {
            list.add(pc);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void consume(Assertable assertable) {
        Class<? extends Assertable> required = assertable.getClass();
        for (AssertableConsumer<?> c : list) {
            Class<?> accepted = c.getAcceptedAssertableClass();
            if (accepted.isAssignableFrom(required)) {
                ((AssertableConsumer<Assertable>) c).consume(assertable);
            }
        }
    }

}
