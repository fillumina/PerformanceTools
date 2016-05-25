package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.ComposedName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceConsumerChain<A>
        extends PerformanceConsumerAggregatorImpl<PerformanceConsumerChain<A>, A>
        implements PerformanceConsumer<A> {

    @Override
    public void consume(ComposedName message, A performances) {
        dispatchToConsumers(message, performances);
    }
}
