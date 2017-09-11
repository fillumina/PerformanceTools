package com.fillumina.performance.infrastructure;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractPerformanceProducerInstrumenter
        <I extends PerformanceProducerInstrumenter<I,S,T,P>, S, T, P>
        extends AbstractPerformanceProducer<I,S,T,P>
        implements PerformanceProducerInstrumenter<I,S,T,P> {

    private PerformanceProducer<?,?,T,P> producer;

    protected PerformanceProducer<?,?,T,P> getProducer() {
        return producer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I instrument(I instrumentable) {
        this.producer = instrumentable;
        return (I) this;
    }

    protected P executeProducer() {
        setAllTests();
        return producer.get();
    }

    protected void setAllTests() {
        producer.clearTests();
        producer.setName(getName());
        producer.addTests(getTests());
    }
}
