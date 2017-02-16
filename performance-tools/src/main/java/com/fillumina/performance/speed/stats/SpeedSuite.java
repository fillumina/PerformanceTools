package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.stats.progression.SpeedProgressionStringGenerator;
import com.fillumina.performance.suite.ParameterizedPerformanceSuite;
import com.fillumina.performance.suite.ParameterizedSequencePerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSuite {

    public static <P> ParameterizedPerformanceSuite<P,SpeedStats,SpeedStats>
            parameterizedSuite() {
        return new ParameterizedPerformanceSuite<>(
                SpeedProgressionStringGenerator.parameterized());
    }

    public static <P,S> ParameterizedSequencePerformanceSuite
                    <P,S,SpeedStats,SpeedStats,SpeedStats>
            parameterizedSequenceSuite() {
        return new ParameterizedSequencePerformanceSuite<>(
                SpeedProgressionStringGenerator.parameterizedSequence());
    }
}
