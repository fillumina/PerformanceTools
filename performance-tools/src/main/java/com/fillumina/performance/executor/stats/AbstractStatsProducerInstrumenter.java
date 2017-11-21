package com.fillumina.performance.executor.stats;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractStatsProducerInstrumenter
        <I extends StatsProducer<I>>
    extends AbstractStatsProducer<I>
    implements StatsProducerInstrumenter<I> {

    private StatsProducer<?> producer;

    protected StatsProducer<?> getProducer() {
        return producer;
    }

    @Override
    public AbstractStatsProducerInstrumenter<I> instrument(
            StatsProducer<?> instrumentable) {
        this.producer = instrumentable;
        return this;
    }

    protected MixedStatsHolder executeProducer() {
        setAllTests();
        return producer.get();
    }

    protected void setAllTests() {
        producer.clearTests();
        producer.setName(getName());
        producer.addTests(getTests());
    }
}
