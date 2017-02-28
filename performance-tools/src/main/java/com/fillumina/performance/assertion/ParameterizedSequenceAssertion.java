package com.fillumina.performance.assertion;

/**
 *
 * @param C return value
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedSequenceAssertion<C, A extends Assertable> {

    ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A>
        forAllSequences();

    ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A>
        forSequenceValue(String sequence);

    C endSequences();
}
