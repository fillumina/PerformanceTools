package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParametrizedSequencePerformance
        <C, A extends AssertableMultiStats> {

    C endSequence();

    AssertParametrizedPerformance<AssertParametrizedSequencePerformanceImpl<C, A>, A>
        forAllSequences();

    AssertParametrizedPerformance<AssertParametrizedSequencePerformanceImpl<C, A>, A>
        forSequenceValue(String sequence);

}
