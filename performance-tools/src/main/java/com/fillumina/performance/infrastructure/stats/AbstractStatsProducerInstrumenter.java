package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducerInstrumenter
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends AbstractPerformanceProducer<I, S, Runnable, MixedAssertableHolder>
    implements StatsProducerInstrumenter<I,S> {

    private StatsProducer<?,S> producer;

    protected StatsProducer<?,S> getProducer() {
        return producer;
    }

    @Override
    public AbstractStatsProducerInstrumenter<I,S> instrument(I instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    protected MixedAssertableHolder executeProducer() {
        setAllTests();
        return producer.get();
    }

    protected void setAllTests() {
        producer.clearTests();
        producer.setName(getName());
        producer.addTests(getTests());
    }
}
