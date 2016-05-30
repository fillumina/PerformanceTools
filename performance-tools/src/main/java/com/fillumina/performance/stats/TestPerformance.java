package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO remove this interface: it's useless
public interface TestPerformance {

    double getConfidence();

    Measure getElapsedNanosecondsPerCycle();

    long getIterations();

    String getName();

    long getOriginalTotalSamples();

    MeasureRatio getPercentage();

    long getTotalTime();

    double getTukeyHsd();
}
