package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerAggregator<A extends Assertable>
        implements AssertableConsumer<A> {

    private final List<AssertableConsumer<A>> list = new ArrayList<>();

    public AssertableConsumerAggregator<A> add(
            AssertableConsumer<A> consumer) {
        list.add(consumer);
        return this;
    }

    @SafeVarargs
    public final AssertableConsumerAggregator<A> addAll(
            AssertableConsumer<A>... consumers) {
        for (AssertableConsumer<A> pc : consumers) {
            list.add(pc);
        }
        return this;
    }

    @Override
    public void consume(A assertable) {
        for (AssertableConsumer<A> pc : list) {
            pc.consume(assertable);
        }
    }

}
