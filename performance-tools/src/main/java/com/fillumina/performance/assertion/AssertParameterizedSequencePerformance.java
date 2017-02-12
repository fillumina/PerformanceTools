package com.fillumina.performance.assertion;

/**
 *
 * @param C return value
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface AssertParameterizedSequencePerformance
            <C, A extends AssertableMultiStats> {

    AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
        forAllSequences();

    AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
        forSequenceValue(String sequence);

    C endSequences();
}
