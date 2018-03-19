package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MismatchedUnitRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Unit<?> first, second;

    public MismatchedUnitRuntimeException(Unit<?> first, Unit<?> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String getMessage() {
        return "unit type mismatch: '" + first.getUnitName() +
                "' vs '" + second.getUnitName() + "'";
    }

    public Unit<?> getFirst() {
        return first;
    }

    public Unit<?> getSecond() {
        return second;
    }
}
