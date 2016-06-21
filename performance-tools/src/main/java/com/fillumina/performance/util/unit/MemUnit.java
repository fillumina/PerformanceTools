package com.fillumina.performance.util.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public enum MemUnit implements Unit {

    B(1), KiB(1 << 10), MiB(1 << 20), GiB(1 << 30);

    public static final MemUnit INSTANCE = B;
    public static final UnitFormatter<MemUnit> FORMATTER =
            new UnitFormatter<>(MemUnit.B);
    public static final List<Unit> LIST =
            Collections.unmodifiableList(Arrays.asList((Unit[])values()));
    final private long factor;

    MemUnit(long factor) {
        this.factor = factor;
    }

    @Override
    public double convert(final double value, final Unit unit) {
        return unit.convertToBase(value) / factor;
    }

    @Override
    public double convertToBase(final double value) {
        return value / factor;
    }

    @Override
    public List<Unit> allAvailableUnitOfMeasures() {
        return LIST;
    }

    @Override
    public Unit getBase() {
        return B;
    }

    @Override
    public UnitFormatter<MemUnit> getFormatter() {
        return FORMATTER;
    }
}
