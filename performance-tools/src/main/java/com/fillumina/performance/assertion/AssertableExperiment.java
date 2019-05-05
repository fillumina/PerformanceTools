package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Contains named measurements that can be checked by
 * {@link ExperimentAssertion}s.
 * <p>
 * It's a collection of measures relative to named experiments.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableExperiment {

    /** @return test names. */
    Collection<? extends CharSequence> getNames();

    /**
     * @return the named measure or null if it doesn't exist.
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
     * @throws {@link NoSuchElementException} if there is no measure.
     */
    default Measure getFirstMeasure() throws NoSuchElementException {
        return getMeasure(getNames().iterator().next());
    }
}
