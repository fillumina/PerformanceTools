package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParameterizedSequencePerformance
        <C, A extends AssertableMultiStats> {

    C endSequence();

    AssertParameterizedPerformance<AssertParameterizedSequencePerformanceImpl<C, A>, A>
        forAllSequences();

    AssertParameterizedPerformance<AssertParameterizedSequencePerformanceImpl<C, A>, A>
        forSequenceValue(String sequence);

}
