package com.fillumina.performance.executor.test;

import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class DrainAssertionError extends AssertionError {

    private static final long serialVersionUID = 1L;

    // should never happen, please inform me if it does.
    public DrainAssertionError(Object value) {
        super("drain assertion error, value=" + Objects.toString(value));
    }

    // should never happen, please inform me if it does.
    public DrainAssertionError(String type, Object value) {
        super("drain assertion error, type=" + type + ", value=" +
                Objects.toString(value));
    }

}
