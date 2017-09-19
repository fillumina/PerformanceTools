package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.ConsumerMock;
import java.io.IOException;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <Assertable>
 */
public class AssertionMock
        extends ConsumerMock<Assertable>
        implements Assertion {

    @Override
    public void check(Assertable assertable) {
        accept(assertable);
    }

    @Override
    public void appendTo(Appendable appendable, Assertable assertable)
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
