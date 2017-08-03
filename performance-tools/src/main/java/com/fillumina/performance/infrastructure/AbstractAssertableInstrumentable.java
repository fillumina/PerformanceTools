package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.instrument.Instrumenter;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertableInstrumentable
            <I extends AbstractAssertableInstrumentable<I>>
        extends AbstractAssertableProducer<I, Runnable>
        implements Instrumenter<StatsProducer>,
                   StatsProducer,
                   Serializable {

    private static final long serialVersionUID = 1L;

    private StatsProducer producer;

    protected StatsProducer getProducer() {
        return producer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I instrument(StatsProducer instrumentable) {
        this.producer = instrumentable;
        return (I) this;
    }

    @Override
    public <T extends Instrumenter<StatsProducer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }

    protected MixedAssertableHolder executeProducer() {
        producer.clearTests();
        producer.setName(getName());
        producer.addTests(getTests());
        return producer.execute();
    }
}
