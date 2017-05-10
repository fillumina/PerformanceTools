package com.fillumina.performance.assertion;

import com.fillumina.performance.util.TName;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestNotFoundException(TName testName) {
        super("test '" + testName.toString() + "' not found.");
    }
}
