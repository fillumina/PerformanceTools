package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import java.util.Collection;

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

    /** @return the named measure or null if it doesn't exist. */
    Measure getMeasure(CharSequence name);

    /** @return true if it doesn't contain any measure. */
    default boolean isEmpty() {
        return getNames().isEmpty();
    }

    /** @return the first measure (useful if there is only one). */
    default Measure getFirstMeasure() {
        return getMeasure(getNames().iterator().next());
    }
}
