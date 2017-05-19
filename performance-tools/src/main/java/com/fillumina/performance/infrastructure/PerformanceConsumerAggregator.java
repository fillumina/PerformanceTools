package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerAggregator<A extends Assertable>
        implements PerformanceConsumer<A> {

    private final List<PerformanceConsumer<A>> list = new ArrayList<>();

    public PerformanceConsumerAggregator<A> add(
            PerformanceConsumer<A> consumer) {
        list.add(consumer);
        return this;
    }

    public PerformanceConsumerAggregator<A> addAll(
            PerformanceConsumer<A>... consumers) {
        for (PerformanceConsumer<A> pc : consumers) {
            list.add(pc);
        }
        return this;
    }

    @Override
    public void consume(A assertable) {
        for (PerformanceConsumer<A> pc : list) {
            pc.consume(assertable);
        }
    }

}
