package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceInstrumentable
            <I extends AbstractPerformanceInstrumentable<I,A>,
             A extends Assertable>
        extends AbstractPerformanceProducer<I, A, Runnable>
        implements Instrumenter<StatsProducer<A>>,
                   StatsProducer<A>,
                   Serializable {

    private static final long serialVersionUID = 1L;

    private StatsProducer<A> producer;

    protected StatsProducer<A> getProducer() {
        return producer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I instrument(StatsProducer<A> instrumentable) {
        this.producer = instrumentable;
        return (I) this;
    }

    @Override
    public <T extends Instrumenter<StatsProducer<A>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    protected PHolder<A> executeProducer() {
        producer.clearTests();
        producer.setName(getName());
        producer.addTests(getTests());
        return producer.execute();
    }
}
