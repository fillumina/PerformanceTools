package com.fillumina.performance.sequence;

import com.fillumina.performance.PerformanceConsumer;
import com.fillumina.performance.PerformanceProducer;
import com.fillumina.performance.timer.LoopPerformances;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceProducer<T extends PerformanceProducer>
        implements PerformanceProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private PerformanceConsumer consumer;
    private LoopPerformances loopPerformances;

    /**
     * The assigned consumer receives the average of the
     * values of the last sequence of tests.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T setPerformanceConsumer(
            final PerformanceConsumer consumer) {
        this.consumer = consumer;
        return (T) this;
    }

    @Override
    public <T extends PerformanceConsumer> T use(T consumer) {
        this.consumer = consumer;
        this.consumer.setPerformances(loopPerformances);
        return consumer;
    }

    protected void setLoopPerformances(final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
    }

    protected void processConsumer() {
        if (consumer != null && loopPerformances != null) {
            consumer.setPerformances(loopPerformances);
            consumer.process();
        }
    }
}
