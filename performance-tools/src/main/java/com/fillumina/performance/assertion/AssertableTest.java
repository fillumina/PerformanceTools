package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableTest {

    /** @return the statistics of the measure taken. */
    Measure getValue();

    /** @return the ratio between the current measure and the slowest. */
    MeasureRatio getRatio();
}
