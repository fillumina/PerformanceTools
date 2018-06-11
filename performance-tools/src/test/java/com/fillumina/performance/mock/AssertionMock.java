package com.fillumina.performance.mock;

import java.io.IOException;
import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.ExperimentAssertion;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <Assertable>
 */
public class AssertionMock
        extends ConsumerMock<AssertableExperiment>
        implements ExperimentAssertion {

    @Override
    public void check(AssertableExperiment assertable) {
        accept(assertable);
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable)
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
