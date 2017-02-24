package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;

/**
 * Contains measurements for named tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Assertable {

    /** @return the measure of the named test or null if it doesn't exist. */
    Measure getValue(String testName);

    /** @return the ratio between the named test and the slower one. */
    MeasureRatio getRatioWithSlowestTest(String testName);
}
