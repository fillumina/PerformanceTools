package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;

/**
 *
 * @author fra
 */
public abstract class
    AbstractFluentPerformanceProducer<T extends FluentPerformanceProducer>
        extends AbstractPerformanceProducer<T>
        implements FluentPerformanceProducer {
    private static final long serialVersionUID = 1L;

    @Override
    public <T extends PerformanceConsumer> T use(final T consumer) {
        consumer.setPerformances(getLoopPerformances());
        setPerformanceConsumer(consumer);
        return consumer;
    }

}
