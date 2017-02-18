package com.fillumina.performance.mem;

import com.fillumina.performance.mem.strgen.UsedMemStatsStringGenerator;
import com.fillumina.performance.suite.ParameterizedPerformanceSuite;
import com.fillumina.performance.suite.ParameterizedSequencePerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSuite {

    public static <P> ParameterizedPerformanceSuite<P,MemStats>
            parameterizedSuite() {
        return new ParameterizedPerformanceSuite<>(
                UsedMemStatsStringGenerator.parameterized());
    }

    public static <P,S> ParameterizedSequencePerformanceSuite<P,S,MemStats>
            parameterizedSequenceSuite() {
        return new ParameterizedSequencePerformanceSuite<>(
                UsedMemStatsStringGenerator.parameterizedSequence());
    }
}
