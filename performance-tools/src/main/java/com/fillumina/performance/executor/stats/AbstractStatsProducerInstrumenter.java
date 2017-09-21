package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducerInstrumenter
                        <I extends StatsProducer<I,S>,
                         S extends Stats<?>>
    extends AbstractStatsProducer<I, S>
    implements StatsProducerInstrumenter<I,S> {

    private StatsProducer<?,?> producer;

    protected StatsProducer<?,?> getProducer() {
        return producer;
    }

    @Override
    public AbstractStatsProducerInstrumenter<I,S> instrument(
            StatsProducer<?,?> instrumentable) {
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
