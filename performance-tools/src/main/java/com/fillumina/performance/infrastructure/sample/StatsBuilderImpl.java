package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.Unit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class StatsBuilderImpl<T extends Stats<?>,
                                       S extends AbstractSample<S,V,T>,
                                       V extends SampleValue,
                                       A extends SampleValueAccumulator>
        implements StatsBuilder<T,S> {


    private final TNameMap<A> accumulators = new TNameMap<>();
    private final Supplier<A> supplier;
    private Unit unit;

    public static class Creator<T extends Stats<?>,
                                       S extends AbstractSample<S,V,T>,
                                       V extends SampleValue,
                                       A extends SampleValueAccumulator>
            extends StatsBuilderImpl<T,S,V,A> {

        private final Function<CollectedMeasures<A>,T> statsCreator;
        private final BiPredicate<A,V> valueAccumulator;

        public Creator(
                Supplier<A> supplier,
                Function<CollectedMeasures<A>, T> statsCreator) {
            this(supplier, null, statsCreator);
        }

        public Creator(
                Supplier<A> supplier,
                BiPredicate<A, V> valueAccumulator,
                Function<CollectedMeasures<A>, T> statsCreator) {
            super(supplier);
            this.valueAccumulator = valueAccumulator;
            this.statsCreator = statsCreator;
        }

        @Override
        protected T createNewStats(CollectedMeasures<A> measures) {
            return statsCreator.apply(measures);
        }

        @Override
        protected void accumulateValue(A accumulator, V value) {
            if (valueAccumulator != null) {
                valueAccumulator.test(accumulator, value);
            }
        }
    }

    public StatsBuilderImpl(Supplier<A> supplier) {
        this.supplier = supplier;
    }

    protected abstract T createNewStats(CollectedMeasures<A> measures);

    protected abstract void accumulateValue(A accumulator, V value);

    /**
     * Builds a {@link TimeStats} out of the collected samples.
     *
     * @param message       The message to addSample to the statistics
     * @param confidence    The confidence used
     * @return              The statistics computed over the collected samples
     */
    @Override
    public T createStats(ListFilter<Double> filter) {
        return createNewStats(
                new CollectedMeasures<>(accumulators, unit, filter));
    }

    @Override
    public void addSample(S sample) {
        sample.getValuesMap().values().forEach(t -> {
            A accumulator = getAccumulator(t.getName());
            accumulator.addValue(t.getValue());
            accumulateValue(accumulator, t);
            if (unit == null) {
                unit = t.getUnit();
            }
        });
    }

    private A getAccumulator(TName name) {
        A accumulator = accumulators.get(name);
        if (accumulator == null) {
            accumulator = supplier.get();
            accumulator.setName(name);
            accumulators.put(name, accumulator);
        }
        return accumulator;
    }
}
