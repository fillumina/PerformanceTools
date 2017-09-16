package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import java.util.Collection;

/**
 * Contains named measurements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertable {

    /** @return true if doesn't contain any measure. */
    boolean isEmpty();

    /** @return test names. */
    Collection<? extends CharSequence> getNames();

    /** @return the measure of the first test (useful if there is only one). */
    default Measure getFirstMeasure() {
        return getMeasure(getNames().iterator().next());
    }

    /** @return the measure of the named test or null if it doesn't exist. */
    Measure getMeasure(CharSequence name);
}
