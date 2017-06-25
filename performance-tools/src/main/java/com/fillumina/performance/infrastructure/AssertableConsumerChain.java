package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.Arrays;

/**
 * Group many {@link AssertableConsumer}s together.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerChain<A extends Assertable>
        implements AssertableConsumer<A> {

    private final Iterable<AssertableConsumer<A>> consumers;

    @SafeVarargs
    public AssertableConsumerChain(AssertableConsumer<A>... consumers) {
        this.consumers = Arrays.asList(consumers);
    }

    public AssertableConsumerChain(Iterable<AssertableConsumer<A>> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void consume(A assertable) {
        for (AssertableConsumer<A> consumer : consumers) {
            consumer.consume(assertable);
        }
    }
}
