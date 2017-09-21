package com.fillumina.performance.assertion;

import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface MultiAssertion extends Assertion {

    void forEach(Assertable assertable, Consumer<Assertion> consumer);
}
