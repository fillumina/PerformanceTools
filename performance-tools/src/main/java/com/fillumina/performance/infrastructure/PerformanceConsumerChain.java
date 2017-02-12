package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiStats;
import java.util.Arrays;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerChain<A extends AssertableMultiStats>
        implements PerformanceConsumer<A> {

    private final Iterable<PerformanceConsumer<A>> consumers;

    public PerformanceConsumerChain(PerformanceConsumer<A>... consumers) {
        this.consumers = Arrays.asList(consumers);
    }

    public PerformanceConsumerChain(Iterable<PerformanceConsumer<A>> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void consume(PerformanceHolder<A> performances) {
        for (PerformanceConsumer<A> consumer : consumers) {
            consumer.consume(performances);
        }
    }
}
