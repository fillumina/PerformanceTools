package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;
import java.util.Arrays;

/**
 * Group many {@link PerformanceConsumer}s together.
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
    public void consume(TName tname, A assertable) {
        for (PerformanceConsumer<A> consumer : consumers) {
            consumer.consume(tname, assertable);
        }
    }
}
