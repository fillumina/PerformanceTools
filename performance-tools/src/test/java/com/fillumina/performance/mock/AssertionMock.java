package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.TName;
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
    public void check(TName tname, A performances) {
        consume(tname, performances);
    }

    @Override
    public void toString(Appendable appendable, A assertable)
            throws IOException {
        if (appendable != null) {
            appendable.append(assertable.toString());
        }
    }
}
