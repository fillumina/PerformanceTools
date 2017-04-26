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
public interface Assertable {

    boolean isEmpty();

    /** @return test names. */
    Collection<String> getTestNames();

    /** @return the measure of the named test or null if it doesn't exist. */
    Measure getMeasure(String testName);

    String getSlowestTestName();

    /** @return the ratio between the named test and the slower one. */
    MeasureRatio getRatioWithSlowestTest(String testName, Ratio confidence);
}
