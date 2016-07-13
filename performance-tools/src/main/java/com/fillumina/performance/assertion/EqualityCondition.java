package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum EqualityCondition {
    SAME("equals to"),
    LESS("less than"),
    GREATER("greater than");

    private final String message;

    EqualityCondition(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
