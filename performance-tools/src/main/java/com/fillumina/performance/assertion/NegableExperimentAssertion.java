package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface NegableExperimentAssertion extends ExperimentAssertion {

    public AbstractExperimentAssertionError getAssertionError(
            AssertableExperiment assertable);

}
