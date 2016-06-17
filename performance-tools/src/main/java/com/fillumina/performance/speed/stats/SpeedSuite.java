package com.fillumina.performance.speed.stats;

import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSuite {

    public static <P> ParametrizedPerformanceSuite<P,PerformanceStats>
            parametrizedSuite() {
        return new ParametrizedPerformanceSuite<>(
                SpeedStringGenerator.parametrized());
    }

    public static <P,S> ParametrizedSequencePerformanceSuite<P,S,PerformanceStats>
            parametrizedSequenceSuite() {
        return new ParametrizedSequencePerformanceSuite<>(
                SpeedStringGenerator.parametrizedSequence());
    }
}
