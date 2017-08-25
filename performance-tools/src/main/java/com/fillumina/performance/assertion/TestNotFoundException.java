package com.fillumina.performance.assertion;

import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestNotFoundException(CharSequence testName) {
        super("test '" + testName.toString() + "' not found.");
    }

    public TestNotFoundException(CharSequence testName,
            Collection<? extends CharSequence> validTestNames ) {
        super("test '" + testName.toString() +
            "' not found, valid tests are: " + validTestNames.toString());
    }
}
