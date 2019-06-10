package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Comparison {
    EQUALS("equals to", "=", false, RelativeOrder.EQUALS),
    NOT_EQUALS("not equals to", "!=", true, RelativeOrder.EQUALS),
    LESS("less than", "<", false, RelativeOrder.LESS),
    LESS_OR_EQUALS("less or equals than", "<=", true, RelativeOrder.GREATER),
    GREATER("greater than", ">", false, RelativeOrder.GREATER),
    GREATER_OR_EQUALS("greater or equals than", ">=", true, RelativeOrder.LESS);

    private final String message;
    private final String symbol;
    private final boolean negate;
    private final RelativeOrder order;

    public static Comparison not(Comparison order) {
        switch (order) {
            case EQUALS: return NOT_EQUALS;
            case NOT_EQUALS: return EQUALS;
            case GREATER: return LESS_OR_EQUALS;
            case LESS_OR_EQUALS: return GREATER;
            case LESS: return GREATER_OR_EQUALS;
            case GREATER_OR_EQUALS: return LESS;
        }
        throw new AssertionError("unmanaged enum value: " + order);
    }

    public static Comparison from(RelativeOrder order) {
        switch (order) {
            case EQUALS: return EQUALS;
            case GREATER: return GREATER;
            case LESS: return LESS;
        }
        throw new AssertionError("unmanaged enum value: " + order);
    }

    public static Comparison from(RelativeOrder order, boolean negate) {
        if (negate) {
            switch (order) {
                case EQUALS: return NOT_EQUALS;
                case GREATER: return LESS_OR_EQUALS;
                case LESS: return GREATER_OR_EQUALS;
            }
        } else {
            switch (order) {
                case EQUALS: return EQUALS;
                case GREATER: return GREATER;
                case LESS: return LESS;
            }
        }
        throw new AssertionError("unmanaged enum value: " + order);
    }

    Comparison(String message, String symbol,
            boolean negate, RelativeOrder order) {
        this.message = message;
        this.symbol = symbol;
        this.negate = negate;
        this.order = order;
    }

    public String getMessage() {
        return message;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isNegate() {
        return negate;
    }

    public RelativeOrder getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return message;
    }
}
