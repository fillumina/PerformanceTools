package com.fillumina.performance.assertion;

import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MultiAssertion<A extends Assertable>
        extends Assertion<A> {

    void iterateAssertions(A assertable, Consumer<Assertion<A>> consumer);
}
