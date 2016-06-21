package com.fillumina.performance.assertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum OrderCondition {
    SAME("same as"),
    LESSER("less than"),
    GREATER("greater than");

    private final String message;

    private OrderCondition(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
