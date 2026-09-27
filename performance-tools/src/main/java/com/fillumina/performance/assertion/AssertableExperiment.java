package com.fillumina.performance.assertion;

import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A collection of measures relative to named experiments.
 *
 * <p>
 * <b>Implementation Note:</b>
 * It can be implemented by extending
 * {@link java.util.HashMap<? extends CharSequence, ? extends DimensionalMeasure>}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableExperiment extends Iterable<DimensionalMeasure> {

    /** @return test names. */
    Collection<? extends CharSequence> getNames();

    /**
     * @return the named measure.
     * @throws NoSuchElementException if there is no measure.
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
    default DimensionalMeasure getFirstMeasure() throws NoSuchElementException {
        return getMeasure(getNames().iterator().next());
    }

    /** @return an iterator over measures. */
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
