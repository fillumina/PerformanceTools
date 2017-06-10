package com.fillumina.performance.template;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Verbosity {
    NO_OUTPUT, OUTPUT_ONLY_RESULTS, MEDIUM_OUTPUT, FULL_OUTPUT;

    public boolean isLessThan(Verbosity v) {
        return ordinal() < v.ordinal();
    }

    public boolean isGreaterThan(Verbosity v) {
        return ordinal() > v.ordinal();
    }
}
