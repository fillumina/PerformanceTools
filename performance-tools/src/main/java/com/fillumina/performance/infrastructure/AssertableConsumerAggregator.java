package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerAggregator
        implements AssertableConsumer<Assertable> {

    private final List<AssertableConsumer<?>> list =
            new CopyOnWriteArrayList<>();

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

    @SafeVarargs
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
        if (assertable != null) {
            for (AssertableConsumer<?> c : list) {
                c.consumeAssertable(assertable);
            }
        }
    }

}
