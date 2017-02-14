package com.fillumina.performance.infrastructure;

import java.util.Arrays;
import com.fillumina.performance.assertion.Assertable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerChain<A extends Assertable>
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
