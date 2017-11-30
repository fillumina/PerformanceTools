package com.fillumina.performance.assertion;

import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MeasureNotFoundException(CharSequence name) {
        super("measure '" + name.toString() + "' not found.");
    }

    public MeasureNotFoundException(CharSequence name,
            Collection<? extends CharSequence> validNames ) {
        super("measure '" + name.toString() +
            "' not found, valid names are: " + validNames.toString());
    }
}
