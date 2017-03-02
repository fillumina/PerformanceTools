package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum OrderCondition {
    SAME("equals to"),
    LESS("less than"),
    GREATER("greater than");

    private final String message;

    OrderCondition(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
