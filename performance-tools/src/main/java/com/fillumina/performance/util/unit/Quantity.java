package com.fillumina.performance.util.unit;

import java.io.Serializable;

/**
 * A value with its dimension.
 * Immutable class.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Quantity<U extends Unit<U>>
        implements Comparable<Quantity<U>>, Serializable {
    private static final long serialVersionUID = 1L;

    private final double value;
    private final U unit;

    @SuppressWarnings("unchecked")
    public static <U extends Unit<U>> Quantity<U> from(double value,
            Unit<?> unit) {
        return new Quantity<>(value, (U)unit);
    }

    public Quantity(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    public double as(Unit<?> targetUnit) {
        return targetUnit.convert(value, unit);
    }

    public Quantity<U> sum(Quantity<U> o) {
        U u = minUnit(o);
        double tot = as(u) + o.as(u);
        return new Quantity<>(tot, u);
    }

    public Quantity<U> subtract(Quantity<U> o) {
        U u = minUnit(o);
        double tot = as(u) - o.as(u);
        return new Quantity<>(tot, u);
    }

    public Quantity<U> multiply(double v) {
        double tot = value * v;
        return new Quantity<>(tot, unit);
    }

    public Quantity<U> divide(double v) {
        double tot = value / v;
        return new Quantity<>(tot, unit);
    }

    public boolean isSameType(Quantity<?> other) {
        return unit.units().getBase() == other.unit.units().getBase();
    }

    public boolean isLessThan(Quantity<U> other) {
        return compareTo(other) == -1;
    }

    public boolean isGreaterThan(Quantity<U> other) {
        return compareTo(other) == 1;
    }

    public boolean isEqualsTo(Quantity<U> other) {
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(Quantity<U> other) {
        U u = minUnit(other);
        return Double.compare(as(u), other.as(u));
    }

    /** NOTE it considers equal values differing by less than 1E-12. */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Quantity)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Quantity<U> other = (Quantity<U>) obj;
        if (!isSameType(other)) {
            return false;
        }
        U u = minUnit(other);
        // takes into account double math approximations
        return Math.abs(as(u) - other.as(u)) < 1E-12;
    }

    @SuppressWarnings("unchecked")
    private U minUnit(Quantity<U> other) {
        if (!isSameType(other)) {
            throw new IllegalArgumentException(
                    "cannot operate on different units: " +
                    unit.toString() + ", " + other.unit.toString());
        }
        return (U) Units.min(unit, other.unit);
    }

    private double toBase() {
        return unit.convertToBase(value);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toBase());
    }

    @Override
    public String toString() {
        return unit.toString(value);
    }

    public String toString(int precision) {
        return unit.toString(value, precision);
    }

    public String toBestString() {
        return unit.toBestString(value);
    }

    public String toBestString(int precision) {
        return unit.toBestString(value, precision);
    }

    public String toPrettyString(int groups) {
        return unit.toPrettyString(value, groups);
    }
}
