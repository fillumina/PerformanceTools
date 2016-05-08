package com.fillumina.performance.stats.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum PercentageCondition {
    EQUALS("equals to"),
    LESS("less than"),
    GREATER("greater than");

    private final String message;

    PercentageCondition(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
