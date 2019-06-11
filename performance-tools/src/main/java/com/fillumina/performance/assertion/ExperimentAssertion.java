package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.io.IOException;

/**
 * Checks if an {@link AssertableExperiment} complies with the requirements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ExperimentAssertion
        extends StringGenerator<AssertableExperiment> {

    /** Always OK assertion. */
    static ExperimentAssertion OK = new ExperimentAssertion() {
        @Override
        public void check(AssertableExperiment assertable) throws AssertionError {
            // do nothing
        }

        @Override
        public void appendTo(Appendable appendable, AssertableExperiment obj)
                throws IOException {
            appendable.append("OK");
        }
    };

    /** Always failing assertion. */
    static ExperimentAssertion FAIL = new ExperimentAssertion() {
        @Override
        public void check(AssertableExperiment assertable) throws AssertionError {
            throw new AssertionError("FAIL");
        }

        @Override
        public void appendTo(Appendable appendable, AssertableExperiment obj)
                throws IOException {
            appendable.append("NOK");
        }
    };

    /**
     * The returned {@link AssertionError} may be specialized to
     * contain detailed information about the failure.
     *
     * @param assertable
     * @throws AssertionError
     */
    public void check(AssertableExperiment assertable)
            throws AssertionError;

    /** @return true if the given {@link AssertableExperiment} complies. */
    default boolean satisfy(AssertableExperiment assertable) {
        try {
            ExperimentAssertion.this.check(assertable);
            return true;
        } catch (AssertionError ignored) {
            return false;
        }
    }

    /**
     * Adds itself to the given {@link failedAssertions} in case of failure.
     *
     * @param assertable            the {@link AssertableExperiment} to check
     * @param catalog      failed assertions for each assertable
     * @param unused   unchecked assertions (to recognize
     *                              unused assertions)
     */
    default void checkAndReport(AssertableExperiment assertable,
            AssertionReport report) {
        report.add(this, assertable);
    }
}
