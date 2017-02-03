package com.fillumina.performance.assertion;

/**
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
