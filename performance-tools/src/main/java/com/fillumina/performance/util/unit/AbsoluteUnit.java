package com.fillumina.performance.util.unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbsoluteUnit implements Unit {
    public static final AbsoluteUnit INSTANCE = new AbsoluteUnit();

    @Override
    public double convert(double value, Unit unit) {
        return value;
    }

    @Override
    public double convertFromBase(double value) {
        return value;
    }

    @Override
    public String toString() {
        return ""; // no unit
    }
}
