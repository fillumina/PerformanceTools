package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public abstract class
    AbstractChainablePerformanceProducer<T extends ChainablePerformanceProducer>
        extends AbstractPerformanceProducer<T>
        implements ChainablePerformanceProducer {
    private static final long serialVersionUID = 1L;

    @Override
    public <T extends PerformanceConsumer> T use(final T consumer) {
        consumer.setPerformances(getLoopPerformances());
        setPerformanceConsumer(consumer);
        return consumer;
    }

}
