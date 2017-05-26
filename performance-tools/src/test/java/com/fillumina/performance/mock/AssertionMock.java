package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import java.io.IOException;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class AssertionMock<A extends Assertable>
        extends ConsumerMock<A>
        implements Assertion<A> {

    @Override
    public void check(A performances) {
        consume(performances);
    }

    @Override
    public void appendTo(Appendable appendable, A assertable)
            throws IOException {
        if (appendable != null) {
            appendable.append(assertable.toString());
        }
    }

    @Override
    public String toString() {
        return "AssertionMock{}";
    }
}
