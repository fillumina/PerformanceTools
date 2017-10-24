package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.Stats;
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
    private final Supplier<A> accumulatorSupplier;
    private Unit<?> unit;

    /**
     * Creates a {@link StatsBuilder} using lambda expressions.
     */
    public static class Creator<T extends Stats<?>,
                                       S extends AbstractSample<S,V,T>,
                                       V extends SampleValue,
                                       A extends SampleValueAccumulator>
            extends StatsBuilderImpl<T,S,V,A> {

        private final Function<CollectedMeasures<A>,T> statsCreator;
        private final BiPredicate<A,V> valueAccumulator;

        public Creator(
                Supplier<A> accumulatorSupplier,
                Function<CollectedMeasures<A>, T> statsCreator) {
            this(accumulatorSupplier, null, statsCreator);
        }

        public Creator(
                Supplier<A> accumulatorSupplier,
                BiPredicate<A, V> valueAccumulator,
                Function<CollectedMeasures<A>, T> statsCreator) {
            super(accumulatorSupplier);
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

    public StatsBuilderImpl(Supplier<A> accumulatorSuppliersupplier) {
        this.accumulatorSupplier = accumulatorSuppliersupplier;
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
        sample.getValuesMap().values().forEach( (V v) -> {
            A accumulator = getAccumulator(v.getName());
            accumulator.addValue(v.getValue());
            accumulateValue(accumulator, v);
            if (unit == null) {
                unit = v.getUnit();
            }
        });
    }

    private A getAccumulator(TName name) {
        A accumulator = accumulators.get(name);
        if (accumulator == null) {
            accumulator = accumulatorSupplier.get();
            accumulator.setName(name);
            accumulators.put(name, accumulator);
        }
        return accumulator;
    }
}
