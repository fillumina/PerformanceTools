package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.CallBackBuilder.Setter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsProducerFactory<C>
        extends CallBackBuilder<C, AbstractStatsProducer<?>> {

    public static final StatsProducerFactory<?> INSTANCE =
            new StatsProducerFactory<Object>();

    private final Setter<StatsProducerFactory<C>,ConfigurableStatsProducer> setter =
            (builtObj) -> {
                StatsProducerFactory.this.statsProducer = builtObj;
                return StatsProducerFactory.this;
            };

    private ConfigurableStatsProducer statsProducer;

    public StatsProducerFactory() {
    }

    public StatsProducerFactory(C caller) {
        super(caller);
    }

    public StatsProducerFactory(Setter<C, AbstractStatsProducer<?>> setter) {
        super(setter);
    }

    public RepeatingStatsProducerBuilder<StatsProducerFactory<C>>
            useRepeatingStrategy() {
        return new RepeatingStatsProducerBuilder<>(setter);
    }

    public FixedSamplesAndIterationsStatsProducerBuilder<StatsProducerFactory<C>>
            useFixedSamplesAndIterationsStrategy() {
        return new FixedSamplesAndIterationsStatsProducerBuilder<>(setter);
    }

    public IncreasingSamplesStatsProducerBuilder<StatsProducerFactory<C>>
            useIncreasingSamplesStrategy() {
        return new IncreasingSamplesStatsProducerBuilder<>(setter);
    }

    @Override
    public AbstractStatsProducer<?> build() {
        return statsProducer;
    }
}
