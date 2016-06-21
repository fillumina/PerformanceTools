package com.fillumina.performance.util.unit;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbsoluteUnit implements Unit {
    public static final AbsoluteUnit INSTANCE = new AbsoluteUnit();

    public static final UnitFormatter<AbsoluteUnit> FORMATTER =
            new UnitFormatter<>(AbsoluteUnit.INSTANCE);

    @Override
    public double convert(double value, Unit unit) {
        return value;
    }

    @Override
    public double convertToBase(double value) {
        return value;
    }

    @Override
    public Unit getBase() {
        return this;
    }

    @Override
    public List<Unit> allAvailableUnitOfMeasures() {
        return Collections.<Unit>emptyList();
    }

    @Override
    public UnitFormatter<? extends Unit> getFormatter() {
        return FORMATTER;
    }

    @Override
    public String toString() {
        return ""; // no unit
    }
}
