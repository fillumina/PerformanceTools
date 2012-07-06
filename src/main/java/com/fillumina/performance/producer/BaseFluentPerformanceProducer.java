package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public class BaseFluentPerformanceProducer<T extends FluentPerformanceProducer>
        extends BasePerformanceProducer<T>
        implements FluentPerformanceProducer {
    private static final long serialVersionUID = 1L;

    public BaseFluentPerformanceProducer() {
    }

    public BaseFluentPerformanceProducer(final LoopPerformances loopPerformances) {
        super(loopPerformances);
    }

    @Override
    public <T extends PerformanceConsumer> T use(final T consumer) {
        consumer.setPerformances(getLoopPerformances());
        return consumer;
    }

}
