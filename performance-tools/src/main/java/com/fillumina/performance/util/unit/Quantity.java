package com.fillumina.performance.util.unit;

/**
 * Record a value with its dimension.
 * This class is immutable.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Quantity<U extends Unit<U>> implements Comparable<Quantity<U>> {
    private final double value;
    private final U unit;

    @SuppressWarnings("unchecked")
    public static <U extends Unit<U>> Quantity<U> create(double value,
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

    public double as(U targetUnit) {
        return targetUnit.convert(value, unit);
    }

    public double toBase() {
        return unit.convertToBase(value);
    }

    public Quantity<U> add(Quantity<U> o) {
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

    public boolean isSameUnit(Quantity<?> quantity) {
        return unit.units().getBase() == quantity.unit.units().getBase();
    }

    @Override
    public int compareTo(Quantity<U> other) {
        U u = minUnit(other);
        return Double.compare(as(u), other.as(u));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Quantity)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Quantity<U> other = (Quantity<U>) obj;
        if (!isSameUnit(other)) {
            return false;
        }
        U u = minUnit(other);
        // takes into account double math approximations
        return Math.abs(as(u) - other.as(u)) < 1E-12;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toBase());
    }

    private U minUnit(Quantity<U> other) {
        if (!isSameUnit(other)) {
            throw new IllegalArgumentException(
                    "cannot operate on different units: " +
                    unit.toString() + ", " + other.unit.toString());
        }
        return unit.units().minUnit(unit, other.unit);
    }

    @Override
    public String toString() {
        return unit.units().toPrettyString(toBase());
    }
}
