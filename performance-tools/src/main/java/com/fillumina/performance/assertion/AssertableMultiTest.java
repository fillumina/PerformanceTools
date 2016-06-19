package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertableMultiTest {
    
    Measure getValue(String testName);

    MeasureRatio getRatioWithSlowestTest(String testName);
}
