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

    public double in(U targetUnit) {
        return targetUnit.convert(value, unit);
    }

    public double toBase() {
        return unit.convertToBase(value);
    }

    public Quantity<U> add(Quantity<U> o) {
        U u = minUnit(o);
        double tot = in(u) + o.in(u);
        return new Quantity<>(tot, u);
    }

    public Quantity<U> subtract(Quantity<U> o) {
        U u = minUnit(o);
        double tot = in(u) - o.in(u);
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

    @Override
    public int compareTo(Quantity<U> o) {
        U u = minUnit(o);
        return Double.compare(in(u), o.in(u));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Quantity)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Quantity<U> other = (Quantity<U>) obj;
        if (unit.units().getBase() != other.unit.units().getBase()) {
            return false;
        }
        U u = minUnit(other);
        // takes account of double calculus error
        return in(u) - other.in(u) < 1E-12;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(toBase());
    }

    private U minUnit(Quantity<U> other) {
        return unit.units().minUnit(unit, other.unit);
    }

    @Override
    public String toString() {
        return unit.units().toPrettyString(toBase());
    }
}
