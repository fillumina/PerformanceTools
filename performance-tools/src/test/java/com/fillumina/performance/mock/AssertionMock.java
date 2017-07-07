package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
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

    public AssertionMock() {
        super(Assertable.class);
    }

    @Override
    public void check(Assertable assertable) {
        consume(assertable);
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
