package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 * Contains measurements of named tests.
 * <p>
 * Performance values are very dependent on the system they are measured on
 * (architecture, CPU, RAM, Operative System, JVM version...) so to give a more
 * versatile and objective value the accent has been given to the ratio
 * between different tests. This would give a more stable and uniform measure.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO make assertable serializable (better as XML)
public interface Assertable {

    /** @return true if doesn't contain any results. */
    boolean isEmpty();

    /** @return test names. */
    Collection<TName> getTestNames();

    /** @return the measure of the first test. */
    default Measure getMeasure() {
        return getMeasure(getTestNames().iterator().next());
    }

    /** @return the measure of the named test or null if it doesn't exist. */
    default Measure getMeasure(String testName) {
        return getMeasure(TN.tname(testName));
    }
    Measure getMeasure(TName testName);

    /** @return the name of the reference test (bigger result value). */
    TName getReferenceTestName();

    /** @return the ratio between the named test and the slower one. */
    default MeasureRatio getRatioToReferenceTest(String testName,
            Ratio confidence) {
        return getRatioToReferenceTest(TN.tname(testName), confidence);
    }
    MeasureRatio getRatioToReferenceTest(TName testName, Ratio confidence);
}
