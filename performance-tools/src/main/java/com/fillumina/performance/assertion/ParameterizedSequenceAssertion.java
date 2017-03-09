package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;

/**
 *
 * @param C return value
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParameterizedSequenceAssertion<C, A extends Assertable>
        extends Assertion<PHolder<PHolder<A>>> {

    ParameterizedAssertion<ParameterizedSequenceAssertion<C, A>, A>
        forAllSequences();

    ParameterizedAssertion<ParameterizedSequenceAssertion<C, A>, A>
        forSequenceValue(String sequence);

    ParameterizedSequenceAssertion<C, A> addAssertion(
            Assertion<PHolder<A>> assertion);

    C endSequences();
}
