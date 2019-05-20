package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * It's a collection of measures relative to named experiments.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableExperiment extends Iterable<DimensionalMeasure> {

    /** @return test names. */
    Collection<? extends CharSequence> getNames();

    /**
     * @return the named measure.
     * @throws {@link NoSuchElementException} if there is no measure.
     */
    DimensionalMeasure getMeasure(CharSequence name)
            throws NoSuchElementException;

    /** @return true if it doesn't contain any measure. */
    default boolean isEmpty() {
        return getNames().isEmpty();
    }

    /**
     * @return the first measure (useful if there is only one).
     * @throws {@link NoSuchElementException} if there aren't any measure.
     */
    default Measure getFirstMeasure() throws NoSuchElementException {
        return getMeasure(getNames().iterator().next());
    }

    @Override
    public default Iterator<DimensionalMeasure> iterator() {
        final Iterator<? extends CharSequence> it = getNames().iterator();
        return new Iterator<DimensionalMeasure>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public DimensionalMeasure next() {
                return getMeasure(it.next());
            }
        };
    }
}
