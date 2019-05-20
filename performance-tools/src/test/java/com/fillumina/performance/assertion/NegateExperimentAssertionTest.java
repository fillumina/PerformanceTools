package com.fillumina.performance.assertion;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NegateExperimentAssertionTest {

    public static class BadExperimentAssertion implements ExperimentAssertion {

        private final boolean bad;

        public BadExperimentAssertion(boolean bad) {
            this.bad = bad;
        }

        @Override
        public void check(AssertableExperiment exp)
                throws ExperimentAssertionError {
            if (bad) {
                throw new ExperimentAssertionError("is bad");
            }
        }

        @Override
        public void appendTo(Appendable appendable, AssertableExperiment obj)
                throws IOException {
            appendable.append("is " + (bad ? "bad" : "good"));
        }
    }

    @Test
    public void shouldNotCheck() {
        ExperimentAssertion ea = new BadExperimentAssertion(true);

        ExperimentAssertion negate = new NegateExperimentAssertion(ea);

        negate.check(new DefaultAssertableExperiment());
    }

    @Test(expected = ExperimentAssertionError.class)
    public void shouldCheckOk() {
        ExperimentAssertion ea = new BadExperimentAssertion(false);

        ExperimentAssertion negate = new NegateExperimentAssertion(ea);

        negate.check(new DefaultAssertableExperiment());
    }

    @Test
    public void shouldAppendNotMessage() {
        ExperimentAssertion ea = new BadExperimentAssertion(false);

        ExperimentAssertion negate = new NegateExperimentAssertion(ea);

        StringBuilder buf = new StringBuilder();
        negate.appendToCatchingException(buf, new DefaultAssertableExperiment());

        assertEquals("not is good", buf.toString());
    }
}
