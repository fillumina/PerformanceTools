package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExperimentAssertionError extends AssertionError {
    private static final long serialVersionUID = 1L;

    private final AssertionErrorInfo<?> info;

    public ExperimentAssertionError(AssertionErrorInfo<?> info) {
        this.info = info;
    }

    public AssertionErrorInfo<?> getInfo() {
        return info;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return info.toString();
    }
}
