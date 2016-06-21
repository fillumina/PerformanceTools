package com.fillumina.performance.mem;

import com.fillumina.performance.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.suite.ParametrizedSequencePerformanceSuite;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSuite {

    public static <P> ParametrizedPerformanceSuite<P,MemStats>
            parametrizedSuite() {
        return new ParametrizedPerformanceSuite<>(
                MemStringGenerator.parametrized());
    }

    public static <P,S> ParametrizedSequencePerformanceSuite<P,S,MemStats>
            parametrizedSequenceSuite() {
        return new ParametrizedSequencePerformanceSuite<>(
                MemStringGenerator.parametrizedSequence());
    }
}
