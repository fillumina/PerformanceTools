package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 * Contains measurements of named tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertable {

    /** @return true if doesn't contain any measure. */
    boolean isEmpty();

    /** @return test names. */
    Collection<? extends CharSequence> getNames();

    /** @return the measure of the first test (useful if there is only one). */
    default Measure getMeasure() {
        return getMeasure(getNames().iterator().next());
    }

    /** @return the measure of the named test or null if it doesn't exist. */
    Measure getMeasure(CharSequence testName);

    /**
     * @return the ratio between the named test and the bigger one.
     */
    MeasureRatio getRatio(CharSequence testName, Ratio confidence);
}
