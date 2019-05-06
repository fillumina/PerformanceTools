package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExperimentAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    public ExperimentAssertionError() {
    }

    public ExperimentAssertionError(String message) {
        super(message);
    }
}
