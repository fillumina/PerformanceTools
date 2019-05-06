package com.fillumina.performance.assertion;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NegateExperimentAssertion implements ExperimentAssertion {
    private static final long serialVersionUID = 1L;

    private final ExperimentAssertion inner;

    public NegateExperimentAssertion(ExperimentAssertion inner) {
        this.inner = inner;
    }

    @Override
    public void check(AssertableExperiment t) {
        try {
            inner.check(t);
        } catch(AssertionError ex) {
            return;
        }
        String message = createMessage(t);
        throw new ExperimentAssertionError(message);
    }

    private String createMessage(AssertableExperiment t) {
        StringBuilder buf = new StringBuilder();
        buf.append("not ");
        try {
            inner.appendTo(buf, t);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return buf.toString();
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assExp)
            throws IOException {
        appendable.append("not ");
        inner.appendTo(appendable, assExp);
    }

}
