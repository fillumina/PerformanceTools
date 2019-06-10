package com.fillumina.performance.assertion;

import com.fillumina.performance.util.StringGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @param failedAssertions      failed assertions for each assertable
     * @param unusedAssertionChecker   unchecked assertions (to recognize
     *                              unused assertions)
     */
    // TODO move this out
    default void checkAndReport(AssertableExperiment assertable,
            Map<AssertableExperiment, List<ExperimentAssertion>> failedAssertions,
            UnusedAssertionChecker unusedAssertionChecker) {
        try {
            if (!satisfy(assertable)) {
                List<ExperimentAssertion> list = failedAssertions.get(assertable);
                if (list == null) {
                    list = new ArrayList<>();
                    failedAssertions.put(assertable, list);
                }
                list.add(this);
            }
            unusedAssertionChecker.setUsed(this);
        } catch (MeasureNotFoundException e) {
            unusedAssertionChecker.setUnused(this);
        }
    }
}
