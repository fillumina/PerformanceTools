package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum RelativeOrder {
    EQUALS("equals to", '='),
    LESS("less than", '<'),
    GREATER("greater than", '>');

    private final String message;
    private final char symbol;

    RelativeOrder(String message, char symbol) {
        this.message = message;
        this.symbol = symbol;
    }

    public String getMessage() {
        return message;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return message;
    }
}
