package com.fillumina.performance.assertion;

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
// TODO move assertable under infrastructure
public interface Assertable {

    /** @return true if doesn't contain any measure. */
    boolean isEmpty();

    /** @return test names. */
    Collection<? extends CharSequence> getTestNames();

    /** @return the measure of the first test (useful if there is only one). */
    default Measure getMeasure() {
        return getMeasure(getTestNames().iterator().next());
    }

    /** @return the measure of the named test or null if it doesn't exist. */
    Measure getMeasure(CharSequence testName);

    /** @return the name of the reference test. */
    CharSequence getReferenceTestName();

    /**
     * @return the ratio between the named test and the reference
     * (default bigger) one.
     */
    default MeasureRatio getRatioToReferenceTest(CharSequence testName,
            Ratio confidence) {
        Measure m = getMeasure(testName);
        if (m == null) {
            throw new TestNotFoundException(testName, getTestNames());
        }
        Measure ref = getMeasure(getReferenceTestName());
        return new MeasureRatio(m, ref, confidence);
    }

    //Assertable applyOperations(List<TestOperation> operations);
}
