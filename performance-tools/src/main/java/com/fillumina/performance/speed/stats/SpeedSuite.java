package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.stats.progression.SpeedProgressionStringGenerator;
import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSuite {

    public static <P> ParametrizedPerformanceSuite<P,SpeedStats>
            parametrizedSuite() {
        return new ParametrizedPerformanceSuite<>(
                SpeedProgressionStringGenerator.parametrized());
    }

    public static <P,S> ParametrizedSequencePerformanceSuite<P,S,SpeedStats>
            parametrizedSequenceSuite() {
        return new ParametrizedSequencePerformanceSuite<>(
                SpeedProgressionStringGenerator.parametrizedSequence());
    }
}
